package com.ithink.dailytodo.Activity

import android.os.Bundle
import com.ithink.dailytodo.BaseActivity
import com.ithink.dailytodo.databinding.ActivityFaqBinding

class FaqActivity : BaseActivity() {

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