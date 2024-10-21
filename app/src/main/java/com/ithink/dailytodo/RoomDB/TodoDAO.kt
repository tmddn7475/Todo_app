package com.ithink.dailytodo.RoomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDAO {
    // TodoEntity
    @Query("select * from todo ORDER BY startDate")
    suspend fun getTodo(): List<TodoEntity>

    @Query("select * from todo where isDone is 1")
    suspend fun getTodoFinished(): List<TodoEntity>

    @Query("select * from todo where isDone is 0")
    suspend fun getTodoNotFinished(): List<TodoEntity>

    @Query("select * from todo where priorityHigh is 1")
    suspend fun getTodoPriority(): List<TodoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 알람은 중복되지 않게 저장
    fun saveTodo(todoEntity: TodoEntity)

    @Query("select * from todo where id = (:id)")
    fun selectOne(id: Long): TodoEntity

    @Update
    suspend fun update(todoEntity: TodoEntity)

    @Delete
    fun delete(todoEntity: TodoEntity)

    @Query("delete from todo")
    fun deleteAll()
}