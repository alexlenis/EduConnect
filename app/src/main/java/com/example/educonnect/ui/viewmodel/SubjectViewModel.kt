package com.example.educonnect.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Subject
import com.example.educonnect.data.repository.SubjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubjectViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubjectRepository
    val subjects = MutableLiveData<List<Subject>>()

    init {
        val subjectDao = AppDatabase.getDatabase(application).subjectDao()
        repository = SubjectRepository(subjectDao)
        loadSubjects()
    }

    fun loadSubjects() {
        viewModelScope.launch(Dispatchers.IO) {
            subjects.postValue(repository.getAllSubjects())
        }
    }

    fun addSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(subject)
            loadSubjects()
        }
    }
}
