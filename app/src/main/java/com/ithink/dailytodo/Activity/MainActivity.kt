package com.ithink.dailytodo.Activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.ithink.dailytodo.Fragment.CalendarFragment
import com.ithink.dailytodo.Fragment.HomeFragment
import com.ithink.dailytodo.Fragment.ProfileFragment
import com.ithink.dailytodo.Fragment.SearchFragment
import com.ithink.dailytodo.R
import com.ithink.dailytodo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.ithink.dailytodo.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var homeFragment: HomeFragment
    lateinit var calendarFragment: CalendarFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var profileFragment: ProfileFragment

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
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 알림 권한 요청
        requestNotificationPermission(this)

        homeFragment = HomeFragment()
        calendarFragment = CalendarFragment()
        searchFragment = SearchFragment()
        profileFragment = ProfileFragment()

        // 프래그먼트 전환
        supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_home -> {
                    replaceFragment(homeFragment)
                    binding.addTodoCardView.visibility = View.VISIBLE
                }
                R.id.bottom_calender -> {
                    replaceFragment(calendarFragment)
                    binding.addTodoCardView.visibility = View.VISIBLE
                }
                R.id.bottom_search -> {
                    replaceFragment(searchFragment)
                    binding.addTodoCardView.visibility = View.GONE
                }
                else -> {
                    replaceFragment(profileFragment)
                    binding.addTodoCardView.visibility = View.GONE
                }
            }
            true
        }

        // addTodoActivity
        binding.addTodo.setOnClickListener {
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
                Toast.makeText(this, getString(R.string.allow_notification), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 뒤로 가기 버튼 누를시
    private var backPressedTime: Long = 0
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            if(fragment is HomeFragment) {
                if (System.currentTimeMillis() > backPressedTime + 2000) {
                    backPressedTime = System.currentTimeMillis()
                    Snackbar.make(findViewById(R.id.main), getString(R.string.tap_back), Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.bottomNavigationView)
                        .setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text))
                        .setBackgroundTint(ContextCompat.getColor(this@MainActivity, R.color.gray)).show()
                } else if (System.currentTimeMillis() <= backPressedTime + 2000) {
                    finishAffinity()
                }
            } else {
                binding.bottomNavigationView.selectedItemId = R.id.bottom_home
                binding.addTodoCardView.visibility = View.VISIBLE
            }
        }
    }
}