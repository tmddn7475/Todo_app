package com.example.todo.Activity.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.todo.Activity.FaqActivity
import com.example.todo.R
import com.example.todo.RoomDB.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingFragment: PreferenceFragmentCompat() {
    private lateinit var db: TodoDatabase

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        db = TodoDatabase.getInstance(requireContext())!!
        setPreferencesFromResource(R.xml.setting, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key: String = preference.key
        when (key) {
            "todo_reset" -> {
                dataReset()
            }
            "dark_mode" -> {
                preference.setOnPreferenceChangeListener{ _, newValue ->
                    Log.d("Test", newValue.toString())
                    when (newValue){
                        "system" -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        }
                        "light" -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                        "dark" -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    }
                    true
                }
            }
            "app_faq" -> {
                val intent = Intent(requireContext(), FaqActivity::class.java)
                startActivity(intent)
            }
            "app_share" -> {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                intent.type = "text/plain"

                val text = "https://github.com/tmddn7475"
                intent.putExtra(Intent.EXTRA_TEXT, text)

                val chooserTitle = "친구에게 공유하기"
                startActivity(Intent.createChooser(intent, chooserTitle))
            }
        }
        return false
    }

    private fun dataReset(){
        val alertEx: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertEx.setMessage("모든 데이터를 초기화하시겠습니까?")
        alertEx.setNegativeButton("예") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                db.todoDAO().deleteAll()
            }
            Toast.makeText(requireContext(), "초기화되었습니다", Toast.LENGTH_SHORT).show()
        }
        alertEx.setPositiveButton("아니요") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = alertEx.create()
        alert.show()
    }
}