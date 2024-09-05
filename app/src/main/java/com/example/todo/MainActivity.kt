package com.example.todo

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.todo.Fragment1.TodayFragment
import com.example.todo.Fragment2.AddTodoFragment
import com.example.todo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var addTodoFragment: AddTodoFragment
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addTodoFragment = AddTodoFragment()

        supportFragmentManager.beginTransaction().replace(R.id.container, TodayFragment()).commit()

        binding.addTodo.setOnClickListener{
            addTodoFragment.show(supportFragmentManager, addTodoFragment.tag)
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private var backPressedTime: Long = 0
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() > backPressedTime + 2000) {
                backPressedTime = System.currentTimeMillis()
                Snackbar.make(findViewById(R.id.main), "뒤로 버튼을 한번 더 누르면 종료됩니다", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.bottomNavigationView).show()
            } else if (System.currentTimeMillis() <= backPressedTime + 2000) {
                finishAffinity()
            }
        }
    }
}