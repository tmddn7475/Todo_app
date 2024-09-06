package com.example.todo.Fragment2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.databinding.FragmentAddTodoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTodoFragment : BottomSheetDialogFragment() {

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
                    }
                }, year, month, day)
            }?.show()
        }

        binding.todoDate2.setOnClickListener{
            context?.let { it1 ->
                DatePickerDialog(it1, { _, year, month, day ->
                    run {
                        binding.todoDate2.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()
                    }
                }, year, month, day)
            }?.show()
        }

        // description
        binding.todoDescription.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (binding.todoDescription.hasFocus()) {
                    v!!.parent.requestDisallowInterceptTouchEvent(true)
                    when (event!!.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return true
                        }
                    }
                }
                return false
            }
        })

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
            val alert: String = binding.todoAlert.text.toString()

            if(title.isEmpty()){
                Toast.makeText(context, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                val todoEntity = TodoEntity(title = title, startDate = startDate, endDate = endDate,
                    startTime = startTime, endTime = endTime, location = location, description = desc, alert = alert)
                addData(todoEntity)
                dismiss()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from<View>(view.parent as View)
        bottomSheetBehavior.maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

        view.findViewById<TextView>(R.id.todo_cancel).setOnClickListener{
            dismiss()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addData(entity: TodoEntity){
        val mainActivity = (activity as MainActivity)
        val todayFragment = mainActivity.todayFragment

        CoroutineScope(Dispatchers.IO).launch {
            db.todoDAO().saveTodo(entity)
            activity?.runOnUiThread{
                if(todayFragment.isAdded){
                    todayFragment.todayListAdapter.addListItem(entity)
                    todayFragment.todayListAdapter.notifyDataSetChanged()
                }
            }
        }

    }
}