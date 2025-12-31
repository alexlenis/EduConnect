package com.example.educonnect.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
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
import java.text.SimpleDateFormat
import java.util.*

class SubjectActivity : AppCompatActivity() {

    private lateinit var adapter: SubjectAdapter
    private lateinit var db: AppDatabase
    private var editingSubject: Subject? = null

    private var selectedDateMillis: Long? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("el"))

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
        val etDate = findViewById<EditText>(R.id.etSubjectDate)
        val btnSave = findViewById<Button>(R.id.btnSave)

        etDate.isFocusable = false
        etDate.setOnClickListener {
            openDatePicker(etDate)
        }

        adapter = SubjectAdapter(
            emptyList(),

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

            onUpdate = { subject ->
                editingSubject = subject
                etName.setText(subject.name)
                etProfessor.setText(subject.professor)
                etSchedule.setText(subject.schedule)
                etSemester.setText(subject.semester.toString())

                selectedDateMillis = subject.dateMillis
                etDate.setText(
                    subject.dateMillis?.let { dateFormat.format(Date(it)) } ?: ""
                )
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
                    db.subjectDao().insert(
                        Subject(
                            name = name,
                            professor = professor,
                            schedule = schedule,
                            semester = semester.toInt(),
                            dateMillis = selectedDateMillis
                        )
                    )
                } else {
                    db.subjectDao().update(
                        editingSubject!!.copy(
                            name = name,
                            professor = professor,
                            schedule = schedule,
                            semester = semester.toInt(),
                            dateMillis = selectedDateMillis
                        )
                    )
                    editingSubject = null
                }

                clearFields(etName, etProfessor, etSchedule, etSemester, etDate)
                loadSubjects()
            }
        }
    }

    private fun openDatePicker(et: EditText) {
        val cal = Calendar.getInstance()
        selectedDateMillis?.let { cal.timeInMillis = it }

        DatePickerDialog(
            this,
            { _, y, m, d ->
                val picked = Calendar.getInstance().apply {
                    set(y, m, d, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedDateMillis = picked.timeInMillis
                et.setText(dateFormat.format(picked.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadSubjects() {
        lifecycleScope.launch(Dispatchers.IO) {
            val subjects = db.subjectDao().getAllSubjects()
            withContext(Dispatchers.Main) {
                adapter.updateData(subjects)
            }
        }
    }

    private suspend fun clearFields(vararg fields: EditText) {
        withContext(Dispatchers.Main) {
            fields.forEach { it.text.clear() }
            selectedDateMillis = null
        }
    }
}
