package com.ithink.dailylist.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ithink.dailylist.Adapter.TodoAdapter
import com.ithink.dailylist.RoomDB.TodoDatabase
import com.ithink.dailylist.RoomDB.TodoEntity
import com.ithink.dailylist.databinding.ActivityPriorityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PriorityActivity : AppCompatActivity() {

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
            val data = db.todoDAO().getTodo() as ArrayList<TodoEntity>
            runOnUiThread{
                for(item in data){
                    if(item.priorityHigh){
                        listAdapter.addListItem(item)
                    }
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