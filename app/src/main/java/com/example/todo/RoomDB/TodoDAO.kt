package com.example.todo.RoomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDAO {
    @Query("select * from todo ORDER BY startDate")
    fun getTodo(): List<TodoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTodo(todoEntity: TodoEntity)

    @Query("select * from todo where id = (:id)")
    fun selectOne(id: Long): TodoEntity

    @Update
    suspend fun update(todoEntity: TodoEntity)

    @Delete
    fun delete(todoEntity: TodoEntity)
}