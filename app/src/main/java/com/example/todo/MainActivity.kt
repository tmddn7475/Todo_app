package com.example.todo

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.todo.Fragment.CalendarFragment
import com.example.todo.Fragment.HomeFragment
import com.example.todo.Fragment.ProfileFragment
import com.example.todo.Fragment.SearchFragment
import com.example.todo.Fragment.AddTodoFragment
import com.example.todo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var addTodoFragment: AddTodoFragment
    private lateinit var binding: ActivityMainBinding
    lateinit var homeFragment: HomeFragment
    lateinit var calendarFragment: CalendarFragment
    lateinit var searchFragment: SearchFragment
    lateinit var profileFragment: ProfileFragment

    // Acticity 변수화 및 반환
    init {
        instance = this
    }
    companion object {
        private var instance: MainActivity? = null
        fun getInstance(): MainActivity? 		{
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeFragment = HomeFragment()
        calendarFragment = CalendarFragment()
        searchFragment = SearchFragment()
        profileFragment = ProfileFragment()
        addTodoFragment = AddTodoFragment()

        // 프래그먼트 전환
        supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()
        binding.bottomNavigationView.setOnItemSelectedListener {
            replaceFragment(
                when (it.itemId) {
                    R.id.bottom_home -> homeFragment
                    R.id.bottom_calender -> calendarFragment
                    R.id.bottom_search -> searchFragment
                    else -> profileFragment
                }
            )
            true
        }

        // addTodoFragment
        binding.addTodo.setOnClickListener{
            if(!addTodoFragment.isAdded){
                addTodoFragment.show(supportFragmentManager, addTodoFragment.tag)
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    // 뒤로 가기 버튼 누를시
    private var backPressedTime: Long = 0
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val fragment = supportFragmentManager.findFragmentById(R.id.container)

            if(fragment is HomeFragment) {
                if (System.currentTimeMillis() > backPressedTime + 2000) {
                    backPressedTime = System.currentTimeMillis()
                    Snackbar.make(findViewById(R.id.main), "뒤로 버튼을 한번 더 누르면 종료됩니다", Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.bottomNavigationView)
                        .setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text))
                        .setBackgroundTint(ContextCompat.getColor(this@MainActivity, R.color.gray)).show()
                } else if (System.currentTimeMillis() <= backPressedTime + 2000) {
                    finishAffinity()
                }
            } else {
                binding.bottomNavigationView.selectedItemId = R.id.bottom_home
            }
        }
    }
}