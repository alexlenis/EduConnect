package com.example.educonnect.data.dao

import androidx.room.*
import com.example.educonnect.data.entity.Assignment

@Dao
interface AssignmentDao {

    @Query("SELECT * FROM assignment")
    suspend fun getAllAssignments(): List<Assignment>

    @Insert
    suspend fun insert(assignment: Assignment)

    @Update
    suspend fun update(assignment: Assignment)

    @Delete
    suspend fun delete(assignment: Assignment)
}
