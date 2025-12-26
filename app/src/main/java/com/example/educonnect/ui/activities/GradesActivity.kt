package com.example.educonnect.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.ui.adapters.GradeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GradesActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: GradeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)

        db = AppDatabase.getDatabase(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerGrades)
        val emptyLayout = findViewById<LinearLayout>(R.id.layoutEmptyGrades)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GradeAdapter(emptyList())
        recyclerView.adapter = adapter

        loadGrades(recyclerView, emptyLayout)
    }

    private fun loadGrades(
        recyclerView: RecyclerView,
        emptyLayout: LinearLayout
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val grades = db.gradeDao().getAllGrades()

            withContext(Dispatchers.Main) {
                if (grades.isEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyLayout.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.updateData(grades)
                }
            }
        }
    }
}
