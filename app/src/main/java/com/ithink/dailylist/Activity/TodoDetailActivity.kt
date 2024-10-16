package com.ithink.dailylist.Activity

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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ithink.dailylist.Object.Command
import com.ithink.dailylist.Dialog.SelectAlarmDialog
import com.ithink.dailylist.Dialog.SelectTimeDialog
import com.ithink.dailylist.Interface.SelectAlarmInterface
import com.ithink.dailylist.Interface.SelectTimeInterface
import com.ithink.dailylist.R
import com.ithink.dailylist.RoomDB.TodoDatabase
import com.ithink.dailylist.RoomDB.TodoEntity
import com.ithink.dailylist.databinding.ActivityTodoDetailBinding
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
        installSplashScreen()
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
                } else {
                    binding.addTodoSwitch.isChecked = false
                    binding.editTime.text = data.startTime
                    binding.editTime2.text = data.endTime
                }
                binding.addTodoSwitch2.isChecked = data.priorityHigh
                binding.editAlarm.text = data.alert
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
                Toast.makeText(this, "해당 일정을 실행 취소로 표시하였습니다", Toast.LENGTH_SHORT).show()
                binding.todoDetailIsDone.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            } else {
                data.doneDate = Command.getToday()
                data.isDone = true
                updateItem(data)
                Toast.makeText(this, "해당 일정을 완료로 표시하였습니다", Toast.LENGTH_SHORT).show()
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
                    "하루 종일\n"
                } else {
                    "${data.startTime} ~ ${data.endTime}\n"
                }
            } else {
                "${data.startDate} ~ ${data.startDate}\n"
            }
            if(data.location.isNotEmpty()) text += "${data.location}\n"
            if(data.description.isNotEmpty()) text += "${data.description}\n"

            val text2 = "https://github.com/tmddn7475"

            intent.putExtra(Intent.EXTRA_TEXT, "$text\n$text2")

            val chooserTitle = "친구에게 공유하기"
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
                data.priorityHigh = binding.addTodoSwitch2.isChecked
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

    private fun updateItem(todoEntity: TodoEntity){
        CoroutineScope(Dispatchers.IO).launch {
            db.todoDAO().update(todoEntity)
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