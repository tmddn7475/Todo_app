package com.ithink.dailylist.Activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ithink.dailylist.Dialog.SelectAlarmDialog
import com.ithink.dailylist.Dialog.SelectTimeDialog
import com.ithink.dailylist.Interface.SelectAlarmInterface
import com.ithink.dailylist.Interface.SelectTimeInterface
import com.ithink.dailylist.Object.Command
import com.ithink.dailylist.RoomDB.TodoDatabase
import com.ithink.dailylist.RoomDB.TodoEntity
import com.ithink.dailylist.databinding.ActivityAddTodoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Calendar

class AddTodoActivity : AppCompatActivity(), SelectTimeInterface, SelectAlarmInterface {

    private lateinit var db: TodoDatabase
    private lateinit var binding: ActivityAddTodoBinding
    private var data: TodoEntity? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = TodoDatabase.getInstance(this@AddTodoActivity)!!

        // 데이터 가져오기
        data = intent.intentSerializable("todoEntity", TodoEntity::class.java)
        if(data != null) copy(data!!)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        binding.todoDate.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()
        binding.todoDate2.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()
        binding.todoTime.text = "${hour}:00"
        binding.todoTime2.text = "${hour+1}:00"

        // 하루종일 체크
        binding.addTodoSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.materialCardView2.visibility = View.GONE
                binding.materialCardView4.visibility = View.GONE
                binding.todoAlarm.text = "알림 없음"
            } else {
                binding.materialCardView2.visibility = View.VISIBLE
                binding.materialCardView4.visibility = View.VISIBLE
                binding.todoAlarm.text = "알림 없음"
            }
        }

        // 날짜 설정
        binding.todoDate.setOnClickListener{
            DatePickerDialog(this, { _, year, month, day ->
                run {
                    binding.todoDate.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()
                    if(Command.compareDates(binding.todoDate.text.toString(), binding.todoDate2.text.toString())){
                        binding.todoDate2.text = binding.todoDate.text.toString()
                    }
                }
            }, year, month, day).show()
        }

        binding.todoDate2.setOnClickListener{
            DatePickerDialog(this, { _, year, month, day ->
                run {
                    binding.todoDate2.text = year.toString() + "." + (month + 1).toString() + "." + day.toString()
                    if(Command.compareDates(binding.todoDate.text.toString(), binding.todoDate2.text.toString())){
                        binding.todoDate2.text = binding.todoDate.text.toString()
                    }
                }
            }, year, month, day).show()
        }

        // 시간 설정
        binding.todoTime.setOnClickListener{
            SelectTimeDialog(binding.todoTime, this).show(supportFragmentManager, "selectTimeDialog")
        }

        binding.todoTime2.setOnClickListener{
            SelectTimeDialog(binding.todoTime2, this).show(supportFragmentManager, "selectTimeDialog")
        }

        // Alarm 설정
        binding.todoAlarm.setOnClickListener{
            SelectAlarmDialog(binding.todoAlarm, this, binding.addTodoSwitch.isChecked)
                .show(supportFragmentManager, "selectAlarmDialog")
        }

        // 취소
        binding.todoCancel.setOnClickListener{
            finish()
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
            val alarm: String = binding.todoAlarm.text.toString()

            if(title.isEmpty()){
                Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                val todoEntity = TodoEntity(title = title, startDate = startDate, endDate = endDate, startTime = startTime,
                    endTime = endTime, location = location, description = desc, alert = alarm, priorityHigh = binding.addTodoSwitch2.isChecked, doneDate = Command.getToday())
                addData(todoEntity)
                if(alarm != "알림 없음") Command.setAlarm(this, todoEntity)
                Command.widgetUpdate(this)
                finish()
            }
        }
    }

    // 할 일 추가
    @SuppressLint("NotifyDataSetChanged")
    private fun addData(entity: TodoEntity){
        CoroutineScope(Dispatchers.IO).launch {
            db.todoDAO().saveTodo(entity)

            runOnUiThread{
                val mainActivity =
                    com.ithink.dailylist.Activity.MainActivity.Companion.getInstance()
                val homeFragment = mainActivity?.homeFragment
                val calendarFragment = mainActivity?.calendarFragment

                if(homeFragment?.isAdded == true){
                    homeFragment.updateTodoList()
                } else if (calendarFragment?.isAdded == true) {
                    calendarFragment.refresh()
                }
            }
        }
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

    private fun copy(todos: TodoEntity) {
        binding.addTodoTitle.setText(todos.title)
        binding.todoDate.text = todos.startDate
        binding.todoDate2.text = todos.endDate
        if(todos.startTime == "all day"){
            binding.addTodoSwitch.isChecked = true
            binding.materialCardView2.visibility = View.GONE
            binding.materialCardView4.visibility = View.GONE
        } else {
            binding.addTodoSwitch.isChecked = false
            binding.materialCardView2.visibility = View.VISIBLE
            binding.materialCardView4.visibility = View.VISIBLE
            binding.todoTime.text = todos.startTime
            binding.todoTime2.text = todos.endTime
        }
        binding.todoAlarm.text = todos.alert
        binding.todoLocation.setText(todos.location)
        binding.todoDescription.setText(todos.description)
        binding.addTodoSwitch2.isChecked = todos.priorityHigh
    }

    private fun <T: Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializableExtra(key, clazz)
        } else {
            this.getSerializableExtra(key) as T?
        }
    }
}