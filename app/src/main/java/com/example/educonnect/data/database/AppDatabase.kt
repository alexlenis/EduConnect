package com.example.educonnect.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.educonnect.data.dao.AssignmentDao
import com.example.educonnect.data.dao.GradeDao
import com.example.educonnect.data.dao.MapPlaceDao
import com.example.educonnect.data.dao.NoteDao
import com.example.educonnect.data.dao.SubjectDao
import com.example.educonnect.data.entity.Assignment
import com.example.educonnect.data.entity.Grade
import com.example.educonnect.data.entity.MapPlace
import com.example.educonnect.data.entity.Note
import com.example.educonnect.data.entity.Subject

@Database(
    entities = [
        Subject::class,
        Assignment::class,
        Grade::class,
        Note::class,
        MapPlace::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun gradeDao(): GradeDao
    abstract fun noteDao(): NoteDao
    abstract fun mapPlaceDao(): MapPlaceDao

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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
