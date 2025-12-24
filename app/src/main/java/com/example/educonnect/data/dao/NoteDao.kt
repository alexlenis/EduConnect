package com.example.educonnect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.educonnect.data.entity.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<Note>

    @Insert
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
