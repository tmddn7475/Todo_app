package com.example.todo.Activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.todo.Activity.setting.SettingActivity
import com.example.todo.Fragment.CalendarFragment
import com.example.todo.Fragment.HomeFragment
import com.example.todo.Fragment.ProfileFragment
import com.example.todo.Fragment.SearchFragment
import com.example.todo.Object.Command
import com.example.todo.R
import com.example.todo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

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
        fun getInstance(): MainActivity? {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Command.getDisplayMode(this)
        // 알림 권한 요청
        requestNotificationPermission(this)

        // 사이드 네비게이션 바
        binding.sideNavigationView.setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.side_faq -> {
                    val intent = Intent(this, FaqActivity::class.java)
                    startActivity(intent)
                }
                R.id.side_setting -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
            return@setNavigationItemSelectedListener false
        }

        homeFragment = HomeFragment()
        calendarFragment = CalendarFragment()
        searchFragment = SearchFragment()
        profileFragment = ProfileFragment()

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
            val intent = Intent(this, AddTodoActivity::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    // 알림 권한 요청
    private fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상에서 알림 권한이 필요
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) { // 알림 권한 요청 코드
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 허용되었을 때
                Log.i("NOTIFICATION", "access granted")
            } else {
                // 권한이 거부되었을 때
                Toast.makeText(this, "알림 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 뒤로 가기 버튼 누를시
    private var backPressedTime: Long = 0
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 사이드 네비게이션 바가 나온 상태일 경우 닫기
            if(binding.main.isDrawerOpen(GravityCompat.START)){
                binding.main.closeDrawers()
            } else {
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


}