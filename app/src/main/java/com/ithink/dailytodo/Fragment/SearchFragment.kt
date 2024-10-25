package com.ithink.dailytodo.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ithink.dailytodo.Adapter.CalendarTodoAdapter
import com.ithink.dailytodo.R
import com.ithink.dailytodo.RoomDB.TodoDatabase
import com.ithink.dailytodo.RoomDB.TodoEntity
import com.ithink.dailytodo.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val calendarTodoAdapter = CalendarTodoAdapter()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: TodoDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        db = TodoDatabase.getInstance(requireContext())!!
        getData(1)

        binding.searchAll.isChecked = true

        binding.searchRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId){
                R.id.search_all -> getData(1)
                R.id.search_done -> getData(2)
                R.id.search_not_done -> getData(3)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = calendarTodoAdapter
        }

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

    @SuppressLint("NotifyDataSetChanged")
    private fun getData(num: Int){
        calendarTodoAdapter.clearList()
        when(num){
            1 -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = db.todoDAO().getTodo() as ArrayList<TodoEntity>
                    activity?.runOnUiThread{
                        for(item in data){
                            calendarTodoAdapter.addListItem(item)
                        }
                        if(calendarTodoAdapter.itemCount == 0){
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                        calendarTodoAdapter.notifyDataSetChanged()
                    }
                }
            }
            2 -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = db.todoDAO().getTodoFinished() as ArrayList<TodoEntity>
                    activity?.runOnUiThread{
                        for(item in data){
                            calendarTodoAdapter.addListItem(item)
                        }
                        if(calendarTodoAdapter.itemCount == 0){
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                        calendarTodoAdapter.notifyDataSetChanged()
                    }
                }

            }
            3 -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = db.todoDAO().getTodoNotFinished() as ArrayList<TodoEntity>
                    activity?.runOnUiThread{
                        for(item in data){
                            calendarTodoAdapter.addListItem(item)
                        }
                        if(calendarTodoAdapter.itemCount == 0){
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                        calendarTodoAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}