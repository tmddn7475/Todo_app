package com.example.todo.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title:String = "",
    var startDate:String = "",
    var startTime:String = "",
    var endDate:String = "",
    var endTime:String = "",
    var location:String = "",
    var description:String = "",
    var isFinished:Boolean = false
)
