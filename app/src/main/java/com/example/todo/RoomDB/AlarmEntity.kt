package com.example.todo.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    var num: Int, // 일련 번호
    var alarmId : Int, // 알람 요청코드
    var time : Long, // 시간
    var title : String // 알람 내용
)
