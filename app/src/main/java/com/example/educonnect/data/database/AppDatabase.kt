package com.example.educonnect.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.educonnect.data.dao.*
import com.example.educonnect.data.entity.*

@Database(
    entities = [
        Subject::class,
        Assignment::class,
        Grade::class,
        Note::class
    ],
    version = 3,
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
                )
                    .fallbackToDestructiveMigration() // dev stage
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
