package com.example.todo.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.example.todo.R

class DonateActivity : AppCompatActivity() {

    private lateinit var billingCilent: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)


    }
}