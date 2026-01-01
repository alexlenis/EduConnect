package com.example.educonnect.data.repository

import com.example.educonnect.data.dao.SubjectDao
import com.example.educonnect.data.entity.Subject

class SubjectRepository(private val subjectDao: SubjectDao) {

    suspend fun getAllSubjects(): List<Subject> {
        return subjectDao.getAllSubjects()
    }

    suspend fun insert(subject: Subject): Long {
        return subjectDao.insert(subject)
    }

    suspend fun update(subject: Subject) {
        subjectDao.update(subject)
    }

    suspend fun delete(subject: Subject) {
        subjectDao.delete(subject)
    }
}
