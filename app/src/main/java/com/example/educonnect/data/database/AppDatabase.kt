package com.example.educonnect.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.educonnect.data.dao.AssignmentDao
import com.example.educonnect.data.dao.GradeDao
import com.example.educonnect.data.dao.NoteDao
import com.example.educonnect.data.dao.SubjectDao
import com.example.educonnect.data.entity.Assignment
import com.example.educonnect.data.entity.Grade
import com.example.educonnect.data.entity.Note
import com.example.educonnect.data.entity.Subject

@Database(
    entities = [
        Subject::class,
        Assignment::class,
        Grade::class,
        Note::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun gradeDao(): GradeDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "educonnect_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
