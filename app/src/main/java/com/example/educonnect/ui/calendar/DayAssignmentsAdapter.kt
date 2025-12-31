package com.example.educonnect.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R

class DayAssignmentsAdapter(
    private val items: List<CalendarEvent>
) : RecyclerView.Adapter<DayAssignmentsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assignment_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = items[position]) {

            is CalendarEvent.AssignmentEvent -> {
                holder.tvTitle.text = "📌 ${item.title}"
                holder.tvDesc.text = item.description
                holder.tvDesc.visibility = View.VISIBLE
            }

            is CalendarEvent.SubjectEvent -> {
                holder.tvTitle.text = "📘 ${item.name}"
                holder.tvDesc.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
