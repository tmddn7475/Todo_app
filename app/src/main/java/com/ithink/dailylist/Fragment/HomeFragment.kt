package com.ithink.dailylist.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ithink.dailylist.Activity.setting.SettingActivity
import com.ithink.dailylist.Adapter.TodoAdapter
import com.ithink.dailylist.RoomDB.TodoDatabase
import com.ithink.dailylist.RoomDB.TodoEntity
import com.ithink.dailylist.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat", "NotifyDataSetChanged", "SetTextI18n")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: TodoDatabase
    private lateinit var todayListAdapter: TodoAdapter

    private var num: Int = 0
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd")
    private val date = Date(System.currentTimeMillis())
    private val simpleDate: String = dateFormat.format(date)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        todayListAdapter = TodoAdapter(this)
        binding.todayList.adapter = todayListAdapter
        db = TodoDatabase.getInstance(requireContext())!!

        updateTodoList()

        binding.todaySetting.setOnClickListener{
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }

        return binding.root
    }
    
    // 리스트 업데이트
    fun updateTodoList(){
        CoroutineScope(Dispatchers.IO).launch {
            todayListAdapter.clearList()
            val data = db.todoDAO().getTodo() as ArrayList<TodoEntity>
            activity?.runOnUiThread{
                num = 0
                for(item in data){
                    if(todayTodos(item.startDate, item.endDate)){
                        todayListAdapter.addListItem(item)
                        num++
                    }
                }
                binding.text1.text = simpleDate
                if(num == 0){
                    binding.todayList.visibility = View.GONE
                } else {
                    binding.todayList.visibility = View.VISIBLE
                }

                if(num <= 1){
                    binding.text2.text = "You have $num task today"
                } else {
                    binding.text2.text = "You have $num tasks today"
                }
                todayListAdapter.notifyDataSetChanged()
            }
        }
    }

    // 오늘 할일 가져오기
    private fun todayTodos(startDate: String, endDate: String): Boolean {
        var bool = false

        val today: Date = dateFormat.parse(simpleDate)!!
        val date1: Date = dateFormat.parse(startDate)!!
        val date2: Date = dateFormat.parse(endDate)!!

        if((today.after(date1) && today.before(date2)) || today == date1 || today == date2){
            bool = true
        }
        return bool
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}