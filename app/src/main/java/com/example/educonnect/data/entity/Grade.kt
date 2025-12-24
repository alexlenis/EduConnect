package com.example.educonnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Grade(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectId: Int,
    val score: Double,
    val date: String
)
