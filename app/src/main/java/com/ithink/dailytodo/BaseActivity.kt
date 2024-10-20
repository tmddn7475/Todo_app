package com.ithink.dailytodo

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.ithink.dailytodo.Object.Command
import java.util.Locale

open class BaseActivity: AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        var languageCode = Command.getLanguage(newBase) ?: Locale.getDefault().language

        if(languageCode == "default" || languageCode == "null"){
            languageCode = Locale.getDefault().language
        }

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
}