package com.example.educonnect.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.entity.Grade

class GradeAdapter(
    private var grades: List<Grade>
) : RecyclerView.Adapter<GradeAdapter.GradeViewHolder>() {

    class GradeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val score: TextView = view.findViewById(R.id.tvScore)
        val date: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grade, parent, false)
        return GradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val grade = grades[position]
        holder.score.text = grade.score.toString()
        holder.date.text = grade.date.toString()
    }

    override fun getItemCount() = grades.size

    fun updateData(newGrades: List<Grade>) {
        grades = newGrades
        notifyDataSetChanged()
    }
}
