package com.example.educonnect.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Assignment
import kotlinx.coroutines.launch

class AssignmentViewModel(application: Application) :
    AndroidViewModel(application) {

    private val dao =
        AppDatabase.getDatabase(application).assignmentDao()

    val assignments: LiveData<List<Assignment>> =
        dao.getAllAssignments()

    fun insert(assignment: Assignment) {
        viewModelScope.launch {
            dao.insert(assignment)
        }
    }

    fun update(assignment: Assignment) {
        viewModelScope.launch {
            dao.update(assignment)
        }
    }

    fun delete(assignment: Assignment) {
        viewModelScope.launch {
            dao.delete(assignment)
        }
    }
}
