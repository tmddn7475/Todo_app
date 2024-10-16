package com.ithink.dailylist.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ithink.dailylist.databinding.ActivityFaqBinding

class FaqActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.faqBackBtn.setOnClickListener{
            finish()
        }
    }
}