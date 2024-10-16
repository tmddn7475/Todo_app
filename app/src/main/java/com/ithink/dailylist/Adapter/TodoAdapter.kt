package com.ithink.dailylist.Adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.ithink.dailylist.R
import com.ithink.dailylist.RoomDB.TodoDatabase
import com.ithink.dailylist.RoomDB.TodoEntity
import com.ithink.dailylist.Activity.TodoDetailActivity
import com.ithink.dailylist.Object.Command
import com.ithink.dailylist.databinding.TodayItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoAdapter(private val viewModelStoreOwner: ViewModelStoreOwner): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

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

    inner class TodoViewHolder(private val binding: TodayItemBinding): RecyclerView.ViewHolder(binding.root) {
        // binding.root에 있는 binding은 앞쪽에 선언했던 변수임
        // viewHolder = 각 리스트 아이템들을 보관하는 객체
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(todoItem : TodoEntity) {
            // 제목
            binding.todayTitle.text = todoItem.title
            binding.todayTitle.setSingleLine(true)
            
            // 작업 완료 시 취소선
            if(todoItem.isDone){
                binding.todayTitle.paintFlags = binding.todayTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            }
            if(todoItem.priorityHigh){
                binding.todayPriority.visibility = View.VISIBLE
            } else {
                binding.todayPriority.visibility = View.GONE
            }

            // 날짜
            if(todoItem.startTime == "all day" && todoItem.startDate == todoItem.endDate){
                binding.todayTime.text = "하루 종일"
            } else if (todoItem.startDate == todoItem.endDate) {
                binding.todayTime.text = todoItem.startTime + " ~ " + todoItem.endTime
            } else {
                binding.todayTime.text = todoItem.startDate + " ~ " + todoItem.endDate
            }

            // 위치
            if(todoItem.location.isEmpty()){
                binding.imageView2.visibility = View.GONE
                binding.todayLocation.visibility = View.GONE
            } else {
                binding.imageView2.visibility = View.VISIBLE
                binding.todayLocation.visibility = View.VISIBLE
                binding.todayLocation.text = todoItem.location
            }
            binding.todayDesc.text = todoItem.description

            // 일정 완료
            binding.todayDone.setOnClickListener{
                if(todoItem.isDone){
                    binding.todayTitle.paintFlags = binding.todayTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    todoItem.isDone = false
                    updateItem(todoItem)
                    notifyDataSetChanged()
                } else {
                    binding.todayTitle.paintFlags = binding.todayTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    todoItem.isDone = true
                    todoItem.doneDate = Command.getToday()
                    updateItem(todoItem)
                    notifyDataSetChanged()
                }
            }

            // item 삭제
            binding.todayDelete.setOnClickListener {
                AlertDialog.Builder(binding.root.context).setMessage("해당 일정을 삭제하시겠습니까?")
                    .setNegativeButton("아니요"){ dialog, _ ->
                        dialog.dismiss()
                    }.setPositiveButton("네") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            roomDatabase.todoDAO().delete(todoItem)
                            list.remove(todoItem)
                            (binding.root.context as Activity).runOnUiThread {
                                notifyDataSetChanged()
                                Toast.makeText(binding.root.context, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                                Command.delAlarm(binding.root.context, todoItem)
                                Command.widgetUpdate(viewModelStoreOwner)
                            }
                        }
                    }.show()
            }

            // 더 보기
            binding.todayMore.setOnClickListener {
                if(binding.todayDesc.visibility == View.GONE && todoItem.description != ""){
                    binding.todayDesc.visibility = View.VISIBLE
                    binding.todayMore.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
                    expandView(binding.cardView3, binding.todayDesc)
                } else {
                    binding.todayDesc.visibility = View.GONE
                    binding.todayMore.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                    collapseView(binding.cardView3, binding.todayDesc)
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, TodoDetailActivity::class.java)
                intent.putExtra("id", todoItem.id)
                binding.root.context.startActivity(intent)
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

    // 애니메이션
    private fun expandView(view: View, textView: TextView) {
        // 현재 높이 측정
        val initialHeight = view.height

        // 원하는 최종 높이 설정 (예: 500px)
        val targetHeight = view.height + measureViewHeight(textView)

        val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }

        // 애니메이션 시간 설정
        animator.duration = 300
        animator.start()
    }

    private fun collapseView(view: View, textView: TextView) {
        val initialHeight = view.height
        val animator = ValueAnimator.ofInt(initialHeight, initialHeight - textView.height)

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.VISIBLE
            }
        })

        animator.duration = 200
        animator.start()
    }

    // View가 GONE 상태에서도 높이 측정 가능
    private fun measureViewHeight(view: View): Int {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        return view.measuredHeight
    }
}