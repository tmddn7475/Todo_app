package com.ithink.dailytodo.Activity.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.preference.PreferenceManager
import com.ithink.dailytodo.Activity.MainActivity
import com.ithink.dailytodo.BaseActivity
import com.ithink.dailytodo.R

class SettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        supportFragmentManager.beginTransaction().replace(R.id.setting_layout, SettingFragment()).commit()

        val settingBackBtn: ImageView = findViewById(R.id.setting_back_btn)
        settingBackBtn.setOnClickListener{
            back()
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this@SettingActivity)
        preferences.registerOnSharedPreferenceChangeListener { _, key ->
            Log.d("TAG", "onSharedPreferenceChanged: $key")
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun back() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            back()
        }
    }
}