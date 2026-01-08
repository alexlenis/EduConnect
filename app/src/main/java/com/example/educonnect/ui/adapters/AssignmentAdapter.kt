package com.example.educonnect.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.entity.Assignment
import com.example.educonnect.ui.bottomsheet.AddAssignmentBottomSheet
import java.text.SimpleDateFormat
import java.util.*

class AssignmentAdapter(
    private var assignments: List<Assignment>,
    private val onDelete: (Assignment) -> Unit
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    inner class AssignmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvDueDate: TextView = view.findViewById(R.id.tvDueDate)
        val btnUpdate: ImageButton = view.findViewById(R.id.btnUpdate)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assignment, parent, false)
        return AssignmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val assignment = assignments[position]

        holder.tvTitle.text = assignment.title
        holder.tvDescription.text = assignment.description

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvDueDate.text = formatter.format(Date(assignment.dueDate))

        // ✏ EDIT → BottomSheet
        holder.btnUpdate.setOnClickListener {
            AddAssignmentBottomSheet(assignment)
                .show(
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager,
                    "EditAssignment"
                )
        }

        // 🗑 DELETE
        holder.btnDelete.setOnClickListener {
            onDelete(assignment)
        }
    }

    override fun getItemCount(): Int = assignments.size

    fun updateData(newAssignments: List<Assignment>) {
        assignments = newAssignments
        notifyDataSetChanged()
    }
}
