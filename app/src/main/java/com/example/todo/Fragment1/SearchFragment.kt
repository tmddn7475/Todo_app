package com.example.todo.Fragment1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.Adapter.CalendarTodoAdapter
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val calendarTodoAdapter = CalendarTodoAdapter()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: TodoDatabase

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        db = TodoDatabase.getInstance(requireContext())!!
        CoroutineScope(Dispatchers.IO).launch {
            calendarTodoAdapter.clearList()
            val data = db.todoDAO().getTodo() as ArrayList<TodoEntity>
            activity?.runOnUiThread{
                for(item in data){
                    calendarTodoAdapter.addListItem(item)
                }
                calendarTodoAdapter.notifyDataSetChanged()
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = calendarTodoAdapter
        }
        calendarTodoAdapter.notifyDataSetChanged()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(query: String): Boolean {
                calendarTodoAdapter.filter.filter(query)
                return false
            }
        })

        return binding.root
    }
}