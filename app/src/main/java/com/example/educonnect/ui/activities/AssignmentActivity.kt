package com.example.educonnect.ui.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Assignment
import kotlinx.coroutines.launch

class AssignmentActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var editingAssignment: Assignment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment)

        db = AppDatabase.getDatabase(this)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val cbCompleted = findViewById<CheckBox>(R.id.cbCompleted)
        val btnSave = findViewById<Button>(R.id.btnSaveAssignment)

        // Αν ήρθαμε για UPDATE
        editingAssignment = intent.getSerializableExtra("assignment") as? Assignment
        editingAssignment?.let {
            etTitle.setText(it.title)
            etDescription.setText(it.description)
            cbCompleted.isChecked = it.completed
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()

            if (title.isBlank()) {
                Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (editingAssignment == null) {
                    // INSERT
                    val assignment = Assignment(
                        title = title,
                        description = description,
                        dueDate = System.currentTimeMillis(),
                        completed = cbCompleted.isChecked,
                        subjectId = 1
                    )
                    db.assignmentDao().insert(assignment)
                    Toast.makeText(this@AssignmentActivity, "Saved", Toast.LENGTH_SHORT).show()
                } else {
                    // UPDATE
                    val updated = editingAssignment!!.copy(
                        title = title,
                        description = description,
                        completed = cbCompleted.isChecked
                    )
                    db.assignmentDao().update(updated)
                    Toast.makeText(this@AssignmentActivity, "Updated", Toast.LENGTH_SHORT).show()
                }

                etTitle.text.clear()
                etDescription.text.clear()
                cbCompleted.isChecked = false
                editingAssignment = null
            }
        }
    }
}
