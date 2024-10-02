package com.example.todo.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // id
    var title:String = "", // 제목
    var startDate:String = "", // 날짜
    var startTime:String = "",
    var endDate:String = "",
    var endTime:String = "",
    var location:String = "",
    var description:String = "",
    var alert: String = "",
    var priorityHigh: Boolean = false,
    var isDone: Boolean = false
) : Serializable
