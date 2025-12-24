package com.example.educonnect.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.ui.adapters.AssignmentAdapter
import kotlinx.coroutines.launch

class AssignmentListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: AssignmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_list)

        db = AppDatabase.getDatabase(this)

        val recycler = findViewById<RecyclerView>(R.id.recyclerAssignments)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = AssignmentAdapter(
            emptyList(),
            onDelete = { assignment ->
                lifecycleScope.launch {
                    db.assignmentDao().delete(assignment)
                    loadAssignments()
                }
            },
            onUpdate = { assignment ->
                lifecycleScope.launch {
                    val updated = assignment.copy(
                        title = assignment.title + " (updated)"
                    )
                    db.assignmentDao().update(updated)
                    loadAssignments()
                }
            }
        )

        recycler.adapter = adapter
        loadAssignments()
    }

    private fun loadAssignments() {
        lifecycleScope.launch {
            adapter.updateData(db.assignmentDao().getAllAssignments())
        }
    }
}
