package com.ithink.dailytodo.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ithink.dailytodo.BaseActivity
import com.ithink.dailytodo.Object.Command
import com.ithink.dailytodo.Dialog.SelectAlarmDialog
import com.ithink.dailytodo.Dialog.SelectTimeDialog
import com.ithink.dailytodo.Interface.SelectAlarmInterface
import com.ithink.dailytodo.Interface.SelectTimeInterface
import com.ithink.dailytodo.R
import com.ithink.dailytodo.RoomDB.TodoDatabase
import com.ithink.dailytodo.RoomDB.TodoEntity
import com.ithink.dailytodo.Dialog.SelectDateDialog
import com.ithink.dailytodo.Interface.SelectDateInterface
import com.ithink.dailytodo.databinding.ActivityTodoDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoDetailActivity : BaseActivity(), SelectTimeInterface, SelectAlarmInterface, SelectDateInterface {
    private lateinit var binding: ActivityTodoDetailBinding
    private lateinit var db: TodoDatabase
    private lateinit var data: TodoEntity
    private var id: Long = 1

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

        binding.todoDetailTitle.isSingleLine = true
        binding.todoDetailTitle.ellipsize = TextUtils.TruncateAt.MARQUEE // 흐르게 만들기
        binding.todoDetailTitle.isSelected = true

        // TodoEntity 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            data = db.todoDAO().selectOne(id)

            runOnUiThread{
                // 중요
                if(data.priorityHigh){
                    binding.todoDetailPriority.visibility = View.VISIBLE
                } else {
                    binding.todoDetailPriority.visibility = View.GONE
                }
                binding.todoDetailTitle.text = data.title
                binding.todoDetailDate1.text = data.startDate
                binding.todoDetailDate2.text = data.endDate
                // 시간
                if(data.startTime == "all day"){
                    binding.todoDetailTime.text = getString(R.string.all_day)
                } else {
                    binding.todoDetailTime.text = "${data.startTime} ~ ${data.endTime}"
                }
                binding.todoDetailAlarm.text = Command.getAlert(this@TodoDetailActivity, data.alert)

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
                if(data.isDone){
                    binding.todoDetailIsDone.setImageResource(R.drawable.baseline_check_box_24)
                } else {
                    binding.todoDetailIsDone.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
                }

                // edit
                binding.editTitle.setText(data.title)
                binding.editDate.text = data.startDate
                binding.editDate2.text = data.endDate

                if(data.startTime == "all day"){
                    binding.addTodoSwitch.isChecked = true
                    binding.materialCardView2.visibility = View.GONE
                    binding.materialCardView4.visibility = View.GONE
                    binding.editTime.text = "12:00"
                    binding.editTime2.text = "12:00"
                } else {
                    binding.addTodoSwitch.isChecked = false
                    binding.editTime.text = data.startTime
                    binding.editTime2.text = data.endTime
                }
                binding.addTodoSwitch2.isChecked = data.priorityHigh
                binding.editAlarm.text = Command.getAlert(this@TodoDetailActivity, data.alert)
                binding.editLocation.setText(data.location)
                binding.editDescription.setText(data.description)
            }
        }

        binding.todoDetailBackBtn.setOnClickListener {
            finish()
        }

        binding.todoDetailIsDone.setOnClickListener {
            if(data.isDone){
                data.isDone = false
                updateItem(data)
                Toast.makeText(this, getString(R.string.cancel_schedule), Toast.LENGTH_SHORT).show()
                binding.todoDetailIsDone.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            } else {
                data.doneDate = Command.getToday()
                data.isDone = true
                updateItem(data)
                Toast.makeText(this, getString(R.string.complete_schedule), Toast.LENGTH_SHORT).show()
                binding.todoDetailIsDone.setImageResource(R.drawable.baseline_check_box_24)
            }
        }

        // 일정 복사
        binding.todoDetailCopyBtn.setOnClickListener {
            finish()
            val intent = Intent(this, AddTodoActivity::class.java)
            intent.putExtra("todoEntity", data)
            startActivity(intent)
        }

        // 일정 공유
        binding.todoDetailShareBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"

            var text = "${data.title}\n"

            text += if(data.startDate == data.endDate){
                if(data.startTime == "all day"){
                    getString(R.string.all_day) + "\n"
                } else {
                    "${data.startTime} ~ ${data.endTime}\n"
                }
            } else {
                "${data.startDate} ~ ${data.startDate}\n"
            }
            if(data.location.isNotEmpty()) text += "${data.location}\n"
            if(data.description.isNotEmpty()) text += "${data.description}\n"

            val text2 = "https://play.google.com/store/apps/details?id=com.ithink.dailytodo"

            intent.putExtra(Intent.EXTRA_TEXT, "$text\n$text2")

            val chooserTitle = ""
            startActivity(Intent.createChooser(intent, chooserTitle))
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
                binding.editAlarm.text = getString(R.string.no_alert)
            } else {
                binding.materialCardView2.visibility = View.VISIBLE
                binding.materialCardView4.visibility = View.VISIBLE
                binding.editAlarm.text = getString(R.string.no_alert)
            }
        }

        // 날짜
        binding.editDate.setOnClickListener{
            SelectDateDialog(binding.editDate, this).show(supportFragmentManager, "selectTimeDialog")
        }

        binding.editDate2.setOnClickListener{
            SelectDateDialog(binding.editDate2, this).show(supportFragmentManager, "selectTimeDialog")
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
                Toast.makeText(this@TodoDetailActivity, getString(R.string.enter_title), Toast.LENGTH_SHORT).show()
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
                data.priorityHigh = binding.addTodoSwitch2.isChecked
                data.alert = Command.setAlert(this, binding.editAlarm.text.toString())
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
                        if(data.alert != "no_alert"){
                            Command.setAlarm(this@TodoDetailActivity, data)
                        }
                        Command.widgetUpdate(this@TodoDetailActivity)
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
            AlertDialog.Builder(binding.root.context).setMessage(getString(R.string.delete_todo))
                .setNegativeButton(getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton(getString(R.string.delete)) { _, _ ->
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
                            Command.delAlarm(this@TodoDetailActivity, data)
                            Command.widgetUpdate(this@TodoDetailActivity)
                            finish()
                        }
                    }
                }.show()
        }
    }

    private fun updateItem(todoEntity: TodoEntity){
        CoroutineScope(Dispatchers.IO).launch {
            db.todoDAO().update(todoEntity)
        }
    }

    override fun selectedDate(textView: TextView, str: String) {
        textView.text = str
        if(Command.compareDates(binding.editDate.text.toString(), binding.editDate2.text.toString())){
            binding.editDate2.text = binding.editDate.text.toString()
        }
    }
    override fun selectedTime(textView: TextView, str: String) {
        textView.text = str
        if(Command.compareTime(binding.editDate.text.toString(), binding.editDate2.text.toString(), binding.editTime.text.toString(), binding.editTime2.text.toString())){
            binding.editTime2.text = str
        }
    }
    override fun selectedAlarm(textView: TextView, str: String) {
        textView.text = str
    }
}