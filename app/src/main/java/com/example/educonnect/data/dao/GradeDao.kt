package com.example.educonnect.data.dao

import androidx.room.*
import com.example.educonnect.data.entity.Grade

@Dao
interface GradeDao {

    @Query("SELECT * FROM grade")
    fun getAllGrades(): List<Grade>

    @Insert
    fun insert(grade: Grade)
}
