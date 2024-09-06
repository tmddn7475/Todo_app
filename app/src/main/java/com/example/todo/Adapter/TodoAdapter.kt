package com.example.todo.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.databinding.TodayItemBinding

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var list : ArrayList<TodoEntity> = ArrayList()
    private lateinit var roomDatabase: TodoDatabase

    fun addListItem(todoItem: TodoEntity) {
        list.add(todoItem)
    }

    inner class TodoViewHolder(private val binding: TodayItemBinding) : RecyclerView.ViewHolder(binding.root) { // binding.root에 있는 binding은 앞쪽에 선언했던 변수임
        // viewHolder = 각 리스트 아이템들을 보관하는 객체
        @SuppressLint("SetTextI18n")
        fun bind(todoItem : TodoEntity) {
            binding.todayTitle.text = todoItem.title
            if(todoItem.startTime == "all day"){
                binding.todayTime.text = "하루 종일"
            } else {
                binding.todayTime.text = todoItem.startTime + " ~ " + todoItem.endTime
            }
            binding.todayLocation.text = todoItem.location
            binding.todayDesc.text = todoItem.description

            binding.todayMore.setOnClickListener{
                if(binding.todayDesc.visibility == View.GONE){
                    binding.todayDesc.visibility = View.VISIBLE
                    binding.todayMore.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
                } else {
                    binding.todayDesc.visibility = View.GONE
                    binding.todayMore.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        val binding = TodayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // roomDatabase 초기화
        roomDatabase = TodoDatabase.getInstance(binding.root.context)!!

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
