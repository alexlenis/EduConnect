package com.example.educonnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val professor: String,
    val schedule: String,
    val semester: Int,

    val dateMillis: Long?,

    // ✅ optional σύνδεση με MapPlace
    val mapPlaceId: Int? = null
) : Serializable
