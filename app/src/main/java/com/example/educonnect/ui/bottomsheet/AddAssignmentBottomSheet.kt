package com.example.educonnect.ui.bottomsheet

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.educonnect.R
import com.example.educonnect.data.entity.Assignment
import com.example.educonnect.ui.viewmodel.AssignmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class AddAssignmentBottomSheet(
    private val assignmentToEdit: Assignment? = null
) : BottomSheetDialogFragment() {

    private lateinit var viewModel: AssignmentViewModel
    private var selectedDateMillis: Long? = null
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottomsheet_add_assignment, container, false)

        viewModel = ViewModelProvider(requireActivity())[AssignmentViewModel::class.java]

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etDeadline = view.findViewById<EditText>(R.id.etDeadline)
        val cbCompleted = view.findViewById<CheckBox>(R.id.cbCompleted)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // EDIT MODE
        assignmentToEdit?.let {
            etTitle.setText(it.title)
            etDescription.setText(it.description)
            cbCompleted.isChecked = it.completed
            selectedDateMillis = it.dueDate
            etDeadline.setText(formatter.format(Date(it.dueDate)))
        }

        etDeadline.setOnClickListener {
            val cal = Calendar.getInstance()
            selectedDateMillis?.let { cal.timeInMillis = it }

            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    cal.set(y, m, d, 0, 0, 0)
                    selectedDateMillis = cal.timeInMillis
                    etDeadline.setText(formatter.format(cal.time))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc = etDescription.text.toString().trim()

            if (title.isBlank() || selectedDateMillis == null) {
                Toast.makeText(requireContext(), "Fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (assignmentToEdit == null) {
                viewModel.insert(
                    Assignment(
                        title = title,
                        description = desc,
                        dueDate = selectedDateMillis!!,
                        completed = cbCompleted.isChecked,
                        subjectId = 1
                    )
                )
            } else {
                viewModel.update(
                    assignmentToEdit.copy(
                        title = title,
                        description = desc,
                        dueDate = selectedDateMillis!!,
                        completed = cbCompleted.isChecked
                    )
                )
            }
            dismiss()
        }

        return view
    }
}
