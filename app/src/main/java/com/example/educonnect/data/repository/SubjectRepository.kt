package com.example.educonnect.data.repository

import com.example.educonnect.data.dao.SubjectDao
import com.example.educonnect.data.entity.Subject

class SubjectRepository(private val subjectDao: SubjectDao) {

    // παίρνει ΟΛΑ τα subjects
    fun getAllSubjects() = subjectDao.getAllSubjects()

    // insert (suspend)
    suspend fun insert(subject: Subject) {
        subjectDao.insert(subject)
    }

    // delete
    suspend fun delete(subject: Subject) {
        subjectDao.delete(subject)
    }
}
