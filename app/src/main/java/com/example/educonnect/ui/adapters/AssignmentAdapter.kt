package com.example.educonnect.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.entity.Assignment
import java.text.SimpleDateFormat
import java.util.*

class AssignmentAdapter(
    private var assignments: List<Assignment>,
    private val onDelete: (Assignment) -> Unit,
    private val onUpdate: (Assignment) -> Unit
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    inner class AssignmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val description: TextView = view.findViewById(R.id.tvDescription)
        val dueDate: TextView = view.findViewById(R.id.tvDueDate)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnUpdate: Button = view.findViewById(R.id.btnUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assignment, parent, false)
        return AssignmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val assignment = assignments[position]

        holder.title.text = assignment.title
        holder.description.text = assignment.description

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.dueDate.text = formatter.format(Date(assignment.dueDate))

        holder.btnDelete.setOnClickListener {
            onDelete(assignment)
        }

        holder.btnUpdate.setOnClickListener {
            onUpdate(assignment)
        }
    }

    override fun getItemCount() = assignments.size

    fun updateData(newAssignments: List<Assignment>) {
        assignments = newAssignments
        notifyDataSetChanged()
    }
}
