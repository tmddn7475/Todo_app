package com.example.todo.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.MainActivity
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.databinding.ActivityTodoDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoDetailBinding
    private lateinit var db: TodoDatabase
    private lateinit var data: TodoEntity

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getLongExtra("todo", 0)
        db = TodoDatabase.getInstance(this)!!

        binding.todoDetailTitle.setSingleLine(true)
        binding.todoDetailTitle.ellipsize = TextUtils.TruncateAt.MARQUEE // 흐르게 만들기
        binding.todoDetailTitle.isSelected = true

        CoroutineScope(Dispatchers.IO).launch {
            data = db.todoDAO().selectOne(id)

            runOnUiThread{
                binding.todoDetailTitle.text = data.title
                binding.todoDetailDate1.text = data.startDate
                binding.todoDetailDate2.text = data.endDate
                if(data.startTime == "all day"){
                    binding.todoDetailTime.text = "하루 종일"
                } else {
                    binding.todoDetailTime.text = "${data.startTime} ~ ${data.endTime}"
                }
                binding.todoDetailAlarm.text = data.alert
                binding.todoDetailLocation.text = data.location
                binding.todoDetailDescription.text = data.description
            }
        }

        binding.todoDetailBackBtn.setOnClickListener {
            finish()
        }

        // 삭제
        binding.todoDetailDeleteBtn.setOnClickListener{
            val mainActivity = MainActivity.getInstance()
            val homeFragment = mainActivity?.homeFragment
            val calendarFragment = mainActivity?.calendarFragment

            AlertDialog.Builder(binding.root.context).setMessage("해당 일정을 삭제하시겠습니까?")
                .setNegativeButton("아니요"){ dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton("네") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        db.todoDAO().delete(data)
                        runOnUiThread {
                            if(homeFragment?.isAdded!!){
                                homeFragment.updateTodoList()
                            } else if (calendarFragment?.isAdded!!) {
                                calendarFragment.refresh()
                            }
                            Toast.makeText(binding.root.context, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }.show()
        }

    }
}