package com.example.educonnect.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.entity.Subject

class SubjectAdapter(
    private var subjects: List<Subject>
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    fun updateData(newSubjects: List<Subject>) {
        subjects = newSubjects
        notifyDataSetChanged()
    }

    inner class SubjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val professor: TextView = view.findViewById(R.id.tvProfessor)
        val semester: TextView = view.findViewById(R.id.tvSemester)
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
        holder.semester.text = "Semester: ${subject.semester}"
    }

    override fun getItemCount() = subjects.size
}
