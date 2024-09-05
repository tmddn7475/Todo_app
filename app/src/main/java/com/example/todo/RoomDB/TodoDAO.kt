package com.example.todo.RoomDB

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TodoDAO {
    @Query("select * from todo")
    fun getPlaylist(): List<TodoEntity>
}