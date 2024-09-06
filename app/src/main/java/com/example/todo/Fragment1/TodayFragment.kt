package com.example.todo.Fragment1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todo.Adapter.TodoAdapter
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.databinding.FragmentTodayBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodayFragment : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: TodoDatabase
    lateinit var todayListAdapter: TodoAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)

        todayListAdapter = TodoAdapter()
        binding.todayList.adapter = todayListAdapter
        db = TodoDatabase.getInstance(requireContext())!!

        CoroutineScope(Dispatchers.IO).launch {
            val data = db.todoDAO().getTodo() as ArrayList<TodoEntity>
            for(item in data){
                todayListAdapter.addListItem(item)
            }
            activity?.runOnUiThread{
                todayListAdapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }

}