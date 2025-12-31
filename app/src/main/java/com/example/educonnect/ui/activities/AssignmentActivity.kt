package com.example.educonnect.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.educonnect.R
import com.example.educonnect.data.entity.Assignment
import com.example.educonnect.ui.viewmodel.AssignmentViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AssignmentActivity : AppCompatActivity() {

    private lateinit var viewModel: AssignmentViewModel
    private var editingAssignment: Assignment? = null

    // Θα κρατάει την επιλεγμένη ημερομηνία σε millis (αυτό μπαίνει στο dueDate)
    private var selectedDueDateMillis: Long? = null

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("el"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment)

        viewModel = ViewModelProvider(this)[AssignmentViewModel::class.java]

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etDeadline = findViewById<EditText>(R.id.etDeadline)
        val cbCompleted = findViewById<CheckBox>(R.id.cbCompleted)
        val btnSave = findViewById<Button>(R.id.btnSaveAssignment)

        // Deadline: μην ανοίγει keyboard, θα ανοίγει date picker
        etDeadline.isFocusable = false
        etDeadline.isClickable = true

        // UPDATE (αν υπάρχει)
        editingAssignment = intent.getSerializableExtra("assignment") as? Assignment
        editingAssignment?.let { ass ->
            etTitle.setText(ass.title)
            etDescription.setText(ass.description)
            cbCompleted.isChecked = ass.completed

            // Φόρτωσε την υπάρχουσα ημερομηνία
            val cal = Calendar.getInstance().apply {
                timeInMillis = ass.dueDate
                normalizeToMidnight()
            }
            selectedDueDateMillis = cal.timeInMillis
            etDeadline.setText(dateFormat.format(cal.time))
        }

        // DatePicker on click
        etDeadline.setOnClickListener {
            openDatePicker(etDeadline)
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isBlank()) {
                Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dueDate = selectedDueDateMillis
            if (dueDate == null) {
                Toast.makeText(this, "Pick a deadline date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editingAssignment == null) {
                val assignment = Assignment(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    completed = cbCompleted.isChecked,
                    subjectId = 1
                )
                viewModel.insert(assignment)
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            } else {
                val updated = editingAssignment!!.copy(
                    title = title,
                    description = description,
                    dueDate = dueDate, // ✅ update και το dueDate
                    completed = cbCompleted.isChecked
                )
                viewModel.update(updated)
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    private fun openDatePicker(etDeadline: EditText) {
        val base = Calendar.getInstance()

        // Αν ήδη έχεις επιλέξει ημερομηνία, άνοιξε εκεί
        selectedDueDateMillis?.let {
            base.timeInMillis = it
        }

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    normalizeToMidnight()
                }
                selectedDueDateMillis = picked.timeInMillis
                etDeadline.setText(dateFormat.format(picked.time))
            },
            base.get(Calendar.YEAR),
            base.get(Calendar.MONTH),
            base.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun Calendar.normalizeToMidnight() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
