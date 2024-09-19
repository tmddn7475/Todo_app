package com.example.todo.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.todo.Object.Command
import com.example.todo.Dialog.SelectAlarmDialog
import com.example.todo.Interface.SelectTimeInterface
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.Dialog.SelectTimeDialog
import com.example.todo.Interface.SelectAlarmInterface
import com.example.todo.databinding.FragmentAddTodoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTodoFragment : BottomSheetDialogFragment(), SelectTimeInterface, SelectAlarmInterface {

    private lateinit var db: TodoDatabase
    private var _binding: FragmentAddTodoBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTodoBinding.inflate(inflater, container, false)

        db = TodoDatabase.getInstance(requireContext())!!

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.todoDate.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()
        binding.todoDate2.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()

        binding.addTodoSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.materialCardView2.visibility = View.GONE
                binding.materialCardView4.visibility = View.GONE
            } else {
                binding.materialCardView2.visibility = View.VISIBLE
                binding.materialCardView4.visibility = View.VISIBLE
            }
        }

        // date
        binding.todoDate.setOnClickListener{
            context?.let { it1 ->
                DatePickerDialog(it1, { _, year, month, day ->
                    run {
                        binding.todoDate.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()
                        if(Command.compareDates(binding.todoDate.text.toString(), binding.todoDate2.text.toString())){
                            binding.todoDate2.text = binding.todoDate.text.toString()
                        }
                    }
                }, year, month, day)
            }?.show()
        }

        binding.todoDate2.setOnClickListener{
            context?.let { it1 ->
                DatePickerDialog(it1, { _, year, month, day ->
                    run {
                        binding.todoDate2.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()
                        if(Command.compareDates(binding.todoDate.text.toString(), binding.todoDate2.text.toString())){
                            binding.todoDate2.text = binding.todoDate.text.toString()
                        }
                    }
                }, year, month, day)
            }?.show()
        }

        // time
        binding.todoTime.setOnClickListener{
            SelectTimeDialog(binding.todoTime, this).show(parentFragmentManager, "selectTimeDialog")
        }

        binding.todoTime2.setOnClickListener{
            SelectTimeDialog(binding.todoTime2, this).show(parentFragmentManager, "selectTimeDialog")
        }

        // Alarm
        binding.todoAlarm.setOnClickListener{
            SelectAlarmDialog(binding.todoAlarm, this).show(parentFragmentManager, "selectAlarmDialog")
        }

        // 일정 저장
        binding.todoSave.setOnClickListener{
            val title: String = binding.addTodoTitle.text.toString()
            val startDate: String = binding.todoDate.text.toString()
            val endDate: String = binding.todoDate2.text.toString()
            val startTime: String
            val endTime: String
            if(binding.addTodoSwitch.isChecked){
                startTime = "all day"
                endTime = "all day"
            } else {
                startTime = binding.todoTime.text.toString()
                endTime= binding.todoTime2.text.toString()
            }
            val location: String = binding.todoLocation.text.toString()
            val desc: String = binding.todoDescription.text.toString()
            val alert: String = binding.todoAlarm.text.toString()



            if(title.isEmpty()){
                Toast.makeText(context, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                val todoEntity = TodoEntity(title = title, startDate = startDate, endDate = endDate,
                    startTime = startTime, endTime = endTime, location = location, description = desc, alert = alert)
                addData(todoEntity)

                if(todoEntity.alert != "알림 없음") setNotification(todoEntity)
                dismiss()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from<View>(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        bottomSheetBehavior.peekHeight = view.measuredHeight
        bottomSheetBehavior.isDraggable = false

        view.findViewById<TextView>(R.id.todo_cancel).setOnClickListener{
            dismiss()
        }
    }

    // 할 일 추가
    @SuppressLint("NotifyDataSetChanged")
    private fun addData(entity: TodoEntity){
        val mainActivity = (activity as MainActivity)
        val homeFragment = mainActivity.homeFragment
        val calendarFragment = mainActivity.calendarFragment

        CoroutineScope(Dispatchers.IO).launch {
            db.todoDAO().saveTodo(entity)
            activity?.runOnUiThread{
                if(homeFragment.isAdded){
                    homeFragment.updateTodoList()
                } else if (calendarFragment.isAdded) {
                    calendarFragment.refresh()
                }
            }
        }
    }

    private fun setNotification(data: TodoEntity){
        val c = Calendar.getInstance()
        val dateArr = data.startDate.split(".")

        c.set(Calendar.YEAR, dateArr[0].toInt())
        c.set(Calendar.MONTH, dateArr[1].toInt()-1)
        c.set(Calendar.DAY_OF_MONTH, dateArr[2].toInt())

        if(data.startTime == "all day"){
            c.set(Calendar.HOUR_OF_DAY, 9)
            c.set(Calendar.MINUTE, 0)
        } else {
            val timeArr = data.startTime.split(":")
            c.set(Calendar.HOUR_OF_DAY, timeArr[0].toInt())
            c.set(Calendar.MINUTE, timeArr[1].toInt())

            when (data.alert) {
                "5분 전" -> {
                    c.add(Calendar.MINUTE, -5)
                }
                "10분 전" -> {
                    c.add(Calendar.MINUTE, -10)
                }
                "1시간 전" -> {
                    c.add(Calendar.HOUR, -1)
                }
                "1일 전" -> {
                    c.add(Calendar.DAY_OF_MONTH, -1)
                }
            }
        }

        Log.i("alert", "success")
        Command.scheduleNotification(requireContext(), c.timeInMillis, data.id.toInt(), data.title)
    }

    override fun selected(textView: TextView, str: String) {
        textView.text = str
        if(Command.compareTime(binding.todoDate.text.toString(), binding.todoDate2.text.toString(), binding.todoTime.text.toString(), binding.todoTime2.text.toString())){
            binding.todoTime2.text = str
        }
    }

    override fun selectedAlarm(textView: TextView, str: String) {
        textView.text = str
    }
}