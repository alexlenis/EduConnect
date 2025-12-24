package com.example.educonnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val subjectId: Int,          // σύνδεση με Subject
    val text: String,            // κείμενο σημείωσης
    val imagePath: String?,      // path εικόνας (από camera/gallery)
    val createdAt: Long          // timestamp
)
