package com.example.educonnect.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.ui.adapters.GradeAdapter
import kotlinx.coroutines.launch

class GradesActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: GradeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)

        db = AppDatabase.getDatabase(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerGrades)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GradeAdapter(emptyList())
        recyclerView.adapter = adapter

        loadGrades()
    }

    private fun loadGrades() {
        lifecycleScope.launch {
            val grades = db.gradeDao().getAllGrades()
            adapter.updateData(grades)
        }
    }
}
