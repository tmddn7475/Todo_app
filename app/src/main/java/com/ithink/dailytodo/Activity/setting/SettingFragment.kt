package com.ithink.dailytodo.Activity.setting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ithink.dailytodo.Activity.DonateActivity
import com.ithink.dailytodo.Activity.FaqActivity
import com.ithink.dailytodo.Activity.MainActivity
import com.ithink.dailytodo.R
import com.ithink.dailytodo.RoomDB.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

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
            "donate" -> {
                val intent = Intent(requireContext(), DonateActivity::class.java)
                startActivity(intent)
            }
            "app_faq" -> {
                val intent = Intent(requireContext(), FaqActivity::class.java)
                startActivity(intent)
            }
            "app_share" -> {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                intent.type = "text/plain"

                val text = getString(R.string.app_share_text) + "\n\nhttps://github.com/tmddn7475"
                intent.putExtra(Intent.EXTRA_TEXT, text)

                val chooserTitle = ""
                startActivity(Intent.createChooser(intent, chooserTitle))
            }
        }
        return false
    }

    private fun dataReset(){
        val alertEx: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertEx.setMessage(getString(R.string.reset_date1))
        alertEx.setNegativeButton(getString(R.string.delete)) { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                db.todoDAO().deleteAll()
            }
            Toast.makeText(requireContext(), getString(R.string.reset), Toast.LENGTH_SHORT).show()
        }
        alertEx.setPositiveButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        val alert = alertEx.create()
        alert.show()
    }
}