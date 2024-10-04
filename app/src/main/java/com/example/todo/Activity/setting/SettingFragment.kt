package com.example.todo.Activity.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.todo.R

class SettingFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
    }
}