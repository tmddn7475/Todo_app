package com.example.todo.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.Activity.TodoDetailActivity
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.databinding.CalendarTodoItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalendarTodoAdapter: RecyclerView.Adapter<CalendarTodoAdapter.ViewHolder>() {
    private var list : ArrayList<TodoEntity> = ArrayList()
    private lateinit var roomDatabase: TodoDatabase

    fun clearList(){
        list.clear()
    }

    fun addListItem(todoItem: TodoEntity) {
        list.add(todoItem)
    }

    private fun updateItem(todoEntity: TodoEntity){
        CoroutineScope(Dispatchers.IO).launch {
            roomDatabase.todoDAO().update(todoEntity)
        }
    }

    inner class ViewHolder(private val binding: CalendarTodoItemBinding): RecyclerView.ViewHolder(binding.root) {
        // viewHolder = 각 리스트 아이템들을 보관하는 객체
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(todoItem : TodoEntity) {
            binding.title.text = todoItem.title
            if(todoItem.isDone){
                binding.checkBox.isChecked = true
            }

            // 날짜
            if(todoItem.startTime == "all day" && todoItem.startDate == todoItem.endDate){
                binding.time.text = "하루 종일"
            } else if (todoItem.startDate == todoItem.endDate) {
                binding.time.text = todoItem.startTime + " ~ " + todoItem.endTime
            } else {
                binding.time.text = "${todoItem.startDate} / ${todoItem.startTime} ~ ${todoItem.endDate} / ${todoItem.endTime}"
            }

            binding.checkBox.setOnClickListener{
                if(todoItem.isDone){
                    todoItem.isDone = false
                    updateItem(todoItem)
                    notifyDataSetChanged()
                } else {
                    todoItem.isDone = true
                    updateItem(todoItem)
                    notifyDataSetChanged()
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, TodoDetailActivity::class.java)
                intent.putExtra("todo", todoItem.id)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CalendarTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // roomDatabase 초기화
        roomDatabase = TodoDatabase.getInstance(binding.root.context)!!

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}