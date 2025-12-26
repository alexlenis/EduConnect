package com.example.educonnect.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Subject
import com.example.educonnect.ui.adapters.SubjectAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectActivity : AppCompatActivity() {

    private lateinit var adapter: SubjectAdapter
    private lateinit var db: AppDatabase
    private var editingSubject: Subject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        db = AppDatabase.getDatabase(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerSubjects)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val etName = findViewById<EditText>(R.id.etSubjectName)
        val etProfessor = findViewById<EditText>(R.id.etProfessor)
        val etSchedule = findViewById<EditText>(R.id.etSchedule)
        val etSemester = findViewById<EditText>(R.id.etSemester)
        val btnSave = findViewById<Button>(R.id.btnSave)

        adapter = SubjectAdapter(
            emptyList(),

            // 🗑 DELETE με confirmation
            onDelete = { subject ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Subject")
                    .setMessage("Are you sure you want to delete this subject?")
                    .setPositiveButton("Yes") { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            db.subjectDao().delete(subject)
                            loadSubjects()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            },

            // ✏ UPDATE
            onUpdate = { subject ->
                editingSubject = subject
                etName.setText(subject.name)
                etProfessor.setText(subject.professor)
                etSchedule.setText(subject.schedule)
                etSemester.setText(subject.semester.toString())
            }
        )

        recyclerView.adapter = adapter

        loadSubjects()

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val professor = etProfessor.text.toString()
            val schedule = etSchedule.text.toString()
            val semester = etSemester.text.toString()

            if (name.isBlank() || semester.isBlank()) {
                Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                if (editingSubject == null) {
                    // INSERT
                    db.subjectDao().insert(
                        Subject(
                            name = name,
                            professor = professor,
                            schedule = schedule,
                            semester = semester.toInt()
                        )
                    )
                } else {
                    // UPDATE
                    db.subjectDao().update(
                        editingSubject!!.copy(
                            name = name,
                            professor = professor,
                            schedule = schedule,
                            semester = semester.toInt()
                        )
                    )
                    editingSubject = null
                }

                clearFields(etName, etProfessor, etSchedule, etSemester)
                loadSubjects()
            }
        }
    }

    private fun loadSubjects() {
        lifecycleScope.launch(Dispatchers.IO) {
            val subjects = db.subjectDao().getAllSubjects()
            withContext(Dispatchers.Main) {
                adapter.updateData(subjects)
            }
        }
    }

    private suspend fun clearFields(
        etName: EditText,
        etProfessor: EditText,
        etSchedule: EditText,
        etSemester: EditText
    ) {
        withContext(Dispatchers.Main) {
            etName.text.clear()
            etProfessor.text.clear()
            etSchedule.text.clear()
            etSemester.text.clear()
        }
    }
}
