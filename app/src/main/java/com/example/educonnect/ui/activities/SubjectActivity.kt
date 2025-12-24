package com.example.educonnect.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Subject
import com.example.educonnect.ui.adapters.SubjectAdapter
import kotlinx.coroutines.launch

class SubjectActivity : AppCompatActivity() {

    private lateinit var adapter: SubjectAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        db = AppDatabase.getDatabase(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerSubjects)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SubjectAdapter(emptyList())
        recyclerView.adapter = adapter

        val etName = findViewById<EditText>(R.id.etSubjectName)
        val etProfessor = findViewById<EditText>(R.id.etProfessor)
        val etSchedule = findViewById<EditText>(R.id.etSchedule)
        val etSemester = findViewById<EditText>(R.id.etSemester)
        val btnSave = findViewById<Button>(R.id.btnSave)

        loadSubjects()

        btnSave.setOnClickListener {
            val subject = Subject(
                name = etName.text.toString(),
                professor = etProfessor.text.toString(),
                schedule = etSchedule.text.toString(),
                semester = etSemester.text.toString().toInt()
            )

            lifecycleScope.launch {
                db.subjectDao().insert(subject)
                loadSubjects()
            }
        }
    }

    private fun loadSubjects() {
        lifecycleScope.launch {
            val subjects = db.subjectDao().getAllSubjects()
            adapter.updateData(subjects)
        }
    }
}

