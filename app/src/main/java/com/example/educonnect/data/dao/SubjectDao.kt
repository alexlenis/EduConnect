package com.example.educonnect.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.educonnect.data.entity.Subject

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subject")
    fun getAllSubjects(): List<Subject>

    @Insert
    suspend fun insert(subject: Subject)

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)
}
