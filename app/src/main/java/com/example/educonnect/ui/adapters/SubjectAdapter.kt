package com.example.educonnect.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.entity.Subject

class SubjectAdapter(
    private var subjects: List<Subject>,
    private val onDelete: (Subject) -> Unit,
    private val onUpdate: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvSubjectName)
        val professor: TextView = view.findViewById(R.id.tvProfessor)
        val semester: TextView = view.findViewById(R.id.tvSemester)
        val btnUpdate: ImageButton = view.findViewById(R.id.btnUpdate)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]

        holder.name.text = subject.name
        holder.professor.text = subject.professor
        holder.semester.text = "Semester ${subject.semester}"

        holder.btnUpdate.setOnClickListener {
            onUpdate(subject)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(subject)
        }
    }

    override fun getItemCount() = subjects.size

    fun updateData(newSubjects: List<Subject>) {
        subjects = newSubjects
        notifyDataSetChanged()
    }
}
