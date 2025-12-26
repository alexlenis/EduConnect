package com.example.educonnect.ui.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.educonnect.R
import com.example.educonnect.data.entity.Assignment
import com.example.educonnect.ui.viewmodel.AssignmentViewModel

class AssignmentActivity : AppCompatActivity() {

    private lateinit var viewModel: AssignmentViewModel
    private var editingAssignment: Assignment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment)

        viewModel = ViewModelProvider(this)[AssignmentViewModel::class.java]

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val cbCompleted = findViewById<CheckBox>(R.id.cbCompleted)
        val btnSave = findViewById<Button>(R.id.btnSaveAssignment)

        // UPDATE (αν υπάρχει)
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

            if (editingAssignment == null) {
                val assignment = Assignment(
                    title = title,
                    description = description,
                    dueDate = System.currentTimeMillis(),
                    completed = cbCompleted.isChecked,
                    subjectId = 1
                )
                viewModel.insert(assignment)
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            } else {
                val updated = editingAssignment!!.copy(
                    title = title,
                    description = description,
                    completed = cbCompleted.isChecked
                )
                viewModel.update(updated)
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}
