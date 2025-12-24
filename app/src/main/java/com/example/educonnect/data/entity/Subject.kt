package com.example.educonnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity που αναπαριστά ένα μάθημα
 */
@Entity
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val professor: String,
    val schedule: String,
    val semester: Int
)
