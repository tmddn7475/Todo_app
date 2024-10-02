package com.example.todo.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.Object.Command
import com.example.todo.Dialog.SelectAlarmDialog
import com.example.todo.Dialog.SelectTimeDialog
import com.example.todo.Interface.SelectAlarmInterface
import com.example.todo.Interface.SelectTimeInterface
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.databinding.ActivityTodoDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class TodoDetailActivity : AppCompatActivity(), SelectTimeInterface, SelectAlarmInterface {

    private lateinit var binding: ActivityTodoDetailBinding
    private lateinit var db: TodoDatabase
    private lateinit var data: TodoEntity
    private var id: Long = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainActivity = MainActivity.getInstance()
        val homeFragment = mainActivity?.homeFragment
        val calendarFragment = mainActivity?.calendarFragment

        db = TodoDatabase.getInstance(this)!!
        id = intent.getLongExtra("id", 1)

        binding.todoDetailTitle.setSingleLine(true)
        binding.todoDetailTitle.ellipsize = TextUtils.TruncateAt.MARQUEE // 흐르게 만들기
        binding.todoDetailTitle.isSelected = true

        // TodoEntity 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            data = db.todoDAO().selectOne(id)

            runOnUiThread{
                if(data.priorityHigh){
                    binding.todoDetailPriority.visibility = View.VISIBLE
                } else {
                    binding.todoDetailPriority.visibility = View.GONE
                }
                binding.todoDetailTitle.text = data.title
                binding.todoDetailDate1.text = data.startDate
                binding.todoDetailDate2.text = data.endDate
                if(data.startTime == "all day"){
                    binding.todoDetailTime.text = "하루 종일"
                } else {
                    binding.todoDetailTime.text = "${data.startTime} ~ ${data.endTime}"
                }
                binding.todoDetailAlarm.text = data.alert

                if(data.location.isEmpty()){
                    binding.todoDetailLocation.visibility = View.GONE
                } else {
                    binding.todoDetailLocation.text = data.location
                }
                if(data.description.isEmpty()){
                    binding.todoDetailDescriptionIcon.visibility = View.GONE
                } else {
                    binding.todoDetailDescription.text = data.description
                }

                // edit
                binding.editTitle.setText(data.title)
                binding.editDate.text = data.startDate
                binding.editDate2.text = data.endDate
                if(data.startTime == "all day"){
                    binding.addTodoSwitch.isChecked = true
                    binding.materialCardView2.visibility = View.GONE
                    binding.materialCardView4.visibility = View.GONE
                } else {
                    binding.addTodoSwitch.isChecked = false
                    binding.editTime.text = data.startTime
                    binding.editTime2.text = data.endTime
                }
                binding.editAlarm.text = data.alert
                binding.editLocation.setText(data.location)
                binding.editDescription.setText(data.description)
            }
        }

        binding.todoDetailBackBtn.setOnClickListener {
            finish()
        }

        // 일정 복사
        binding.todoDetailCopyBtn.setOnClickListener {
            finish()
            val intent = Intent(this, AddTodoActivity::class.java)
            intent.putExtra("todoEntity", data)
            startActivity(intent)
        }

        // 일정 수정
        binding.todoDetailEditBtn.setOnClickListener{
            binding.toolbar.visibility = View.GONE
            binding.scrollView.visibility = View.GONE
            binding.linearLayout3.visibility = View.GONE
            binding.editScroll.visibility = View.VISIBLE
            binding.editLinear.visibility = View.VISIBLE
        }

        binding.addTodoSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.materialCardView2.visibility = View.GONE
                binding.materialCardView4.visibility = View.GONE
                binding.editAlarm.text = "알림 없음"
            } else {
                binding.materialCardView2.visibility = View.VISIBLE
                binding.materialCardView4.visibility = View.VISIBLE
                binding.editAlarm.text = "알림 없음"
            }
        }

        // 날짜
        binding.editDate.setOnClickListener{
            val cal = Calendar.getInstance()
            val data = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                binding.editDate.text = "${year}.${month+1}.${day}"
                if(Command.compareDates(binding.editDate.text.toString(), binding.editDate2.text.toString())){
                    binding.editDate2.text = binding.editDate.text.toString()
                }
            }
            DatePickerDialog(this, data, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.editDate2.setOnClickListener{
            val cal = Calendar.getInstance()
            val data = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                binding.editDate2.text = "${year}.${month+1}.${day}"
                if(Command.compareDates(binding.editDate.text.toString(), binding.editDate2.text.toString())){
                    binding.editDate2.text = binding.editDate.text.toString()
                }
            }
            DatePickerDialog(this, data, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // time
        binding.editTime.setOnClickListener{
            SelectTimeDialog(binding.editTime, this).show(supportFragmentManager, "selectTimeDialog")
        }

        binding.editTime2.setOnClickListener{
            SelectTimeDialog(binding.editTime2, this).show(supportFragmentManager, "selectTimeDialog")
        }

        // 일정 Alarm
        binding.editAlarm.setOnClickListener{
            SelectAlarmDialog(binding.editAlarm, this, binding.addTodoSwitch.isChecked).show(supportFragmentManager, "selectAlarmDialog")
        }

        // 일정 수정본 저장
        binding.editSave.setOnClickListener{
            if(binding.editTitle.text.trim().isEmpty()){
                Toast.makeText(this@TodoDetailActivity, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                data.title = binding.editTitle.text.toString()
                data.startDate = binding.editDate.text.toString()
                data.endDate = binding.editDate2.text.toString()
                if(binding.addTodoSwitch.isChecked){
                    data.startTime = "all day"
                    data.endTime = "all day"
                } else {
                    data.startTime = binding.editTime.text.toString()
                    data.endTime = binding.editTime2.text.toString()
                }
                data.alert = binding.editAlarm.text.toString()
                data.location = binding.editLocation.text.toString()
                data.description = binding.editDescription.text.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    db.todoDAO().update(data)
                    runOnUiThread{
                        if(mainActivity != null){
                            if(homeFragment?.isAdded!!){
                                homeFragment.updateTodoList()
                            } else if (calendarFragment?.isAdded!!) {
                                calendarFragment.refresh()
                            }
                        }
                        // 알림
                        Command.delAlarm(this@TodoDetailActivity, data)
                        if(data.alert != "알림 없음"){
                            Command.setAlarm(this@TodoDetailActivity, data)
                        }
                        Command.widgetUpdate(this@TodoDetailActivity)
                        Toast.makeText(this@TodoDetailActivity, "수정되었습니다", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        binding.editCancel.setOnClickListener{
            finish()
        }

        // 일정 삭제
        binding.todoDetailDeleteBtn.setOnClickListener{
            AlertDialog.Builder(binding.root.context).setMessage("해당 일정을 삭제하시겠습니까?")
                .setNegativeButton("아니요"){ dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton("네") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        db.todoDAO().delete(data)
                        runOnUiThread {
                            if(mainActivity != null){
                                if(homeFragment?.isAdded!!){
                                    homeFragment.updateTodoList()
                                } else if (calendarFragment?.isAdded!!) {
                                    calendarFragment.refresh()
                                }
                            }
                            Command.widgetUpdate(this@TodoDetailActivity)
                            Toast.makeText(binding.root.context, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }.show()
        }
    }
    override fun selected(textView: TextView, str: String) {
        textView.text = str
        if(Command.compareTime(binding.editDate.text.toString(), binding.editDate2.text.toString(), binding.editTime.text.toString(), binding.editTime2.text.toString())){
            binding.editTime2.text = str
        }
    }

    override fun selectedAlarm(textView: TextView, str: String) {
        textView.text = str
    }
}