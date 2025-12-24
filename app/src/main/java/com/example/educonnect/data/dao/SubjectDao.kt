package com.example.educonnect.data.dao

import androidx.room.*
import com.example.educonnect.data.entity.Subject

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subject")
    fun getAllSubjects(): List<Subject>

    @Insert
    suspend fun insert(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)
}
