package com.example.educonnect.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Subject
import com.example.educonnect.ui.adapters.SubjectAdapter
import com.example.educonnect.ui.bottomsheet.AddSubjectBottomSheet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: SubjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        db = AppDatabase.getDatabase(this)

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerSubjects)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SubjectAdapter(
            subjects = emptyList(),
            onDelete = { subject ->
                showDeleteDialog(subject)
            },
            onUpdate = { subject ->

                AddSubjectBottomSheet(
                    subject = subject,
                    onSaved = { loadSubjects() }
                ).show(supportFragmentManager, "edit_subject")
            }
        )

        recyclerView.adapter = adapter

        // ➕ ADD (FAB)
        findViewById<FloatingActionButton>(R.id.fabAddSubject).setOnClickListener {
            AddSubjectBottomSheet(
                subject = null,
                onSaved = { loadSubjects() }
            ).show(supportFragmentManager, "add_subject")
        }

        loadSubjects()
    }

    private fun loadSubjects() {
        lifecycleScope.launch(Dispatchers.IO) {
            val subjects = db.subjectDao().getAllSubjects()
            withContext(Dispatchers.Main) {
                adapter.updateData(subjects)
            }
        }
    }

    private fun showDeleteDialog(subject: Subject) {
        AlertDialog.Builder(this)
            .setTitle("Delete Subject")
            .setMessage("Are you sure you want to delete this subject?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    db.subjectDao().delete(subject)
                    loadSubjects()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
