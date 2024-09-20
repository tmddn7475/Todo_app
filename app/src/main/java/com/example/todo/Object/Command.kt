package com.example.todo.Object

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.todo.Alarm
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

object Command {
    @SuppressLint("SimpleDateFormat")
    fun compareDates(selectedDate: String, comparisonDate: String): Boolean{
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        var bool = false
        try {
            // 문자열을 Date 객체로 변환
            val date1: Date = dateFormat.parse(selectedDate)!!
            val date2: Date = dateFormat.parse(comparisonDate)!!

            if(date1.after(date2)){
                bool = true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return bool
    }

    @SuppressLint("SimpleDateFormat")
    fun compareTime(selectedDate: String, comparisonDate: String, selectTime: String, comparisonTime: String): Boolean{
        val dateFormat = SimpleDateFormat("HH:mm")
        var bool = false
        try {
            // 문자열을 Date 객체로 변환
            val time1: Date = dateFormat.parse(selectTime)!!
            val time2: Date = dateFormat.parse(comparisonTime)!!

            if(time1.after(time2) && selectedDate == comparisonDate){
                bool = true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return bool
    }
}