package com.example.educonnect.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.entity.Assignment
import com.example.educonnect.ui.adapters.AssignmentAdapter
import com.example.educonnect.ui.viewmodel.AssignmentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AssignmentListActivity : AppCompatActivity() {

    private lateinit var viewModel: AssignmentViewModel
    private lateinit var adapter: AssignmentAdapter
    private val assignments = mutableListOf<Assignment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_list)

        Log.d("ASSIGNMENT_DEBUG", "AssignmentListActivity OPENED")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[AssignmentViewModel::class.java]

        adapter = AssignmentAdapter(
            assignments = assignments,

            // 🗑 DELETE με επιβεβαίωση
            onDelete = { assignment ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Assignment")
                    .setMessage("Are you sure you want to delete this assignment?")
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.delete(assignment)
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            },

            // ✏ UPDATE
            onUpdate = { assignment ->
                val intent = Intent(this, AssignmentActivity::class.java)
                intent.putExtra("assignment", assignment)
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        viewModel.assignments.observe(this) { list ->
            Log.d("ASSIGNMENT_DEBUG", "OBSERVE CALLED - LIST SIZE = ${list.size}")
            assignments.clear()
            assignments.addAll(list)
            adapter.notifyDataSetChanged()
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AssignmentActivity::class.java))
        }
    }
}
