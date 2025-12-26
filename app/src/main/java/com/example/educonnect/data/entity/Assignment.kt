package com.example.educonnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Assignment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subjectId: Int,
    val description: String,
    val dueDate: Long,
    val completed: Boolean
) : Serializable
