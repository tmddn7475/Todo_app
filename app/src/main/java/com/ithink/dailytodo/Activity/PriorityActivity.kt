package com.ithink.dailytodo.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ithink.dailytodo.Adapter.TodoAdapter
import com.ithink.dailytodo.BaseActivity
import com.ithink.dailytodo.RoomDB.TodoDatabase
import com.ithink.dailytodo.RoomDB.TodoEntity
import com.ithink.dailytodo.databinding.ActivityPriorityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PriorityActivity : BaseActivity() {
    private lateinit var binding: ActivityPriorityBinding
    private lateinit var db: TodoDatabase
    private lateinit var listAdapter: TodoAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityPriorityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TodoDatabase.getInstance(this)!!
        listAdapter = TodoAdapter(this)
        binding.list.adapter = listAdapter

        binding.priorityBackBtn.setOnClickListener{
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            listAdapter.clearList()
            val data = db.todoDAO().getTodoPriority() as ArrayList<TodoEntity>
            runOnUiThread {
                for(item in data){
                    listAdapter.addListItem(item)
                }
                listAdapter.notifyDataSetChanged()
                if(listAdapter.itemCount == 0){
                    binding.text.visibility = View.VISIBLE
                } else {
                    binding.text.visibility = View.GONE
                }
            }
        }
    }
}