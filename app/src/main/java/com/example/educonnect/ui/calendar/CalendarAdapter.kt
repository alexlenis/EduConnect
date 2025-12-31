package com.example.educonnect.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R

class CalendarAdapter(
    private var days: List<CalendarDayModel>,
    private val onDayClick: (CalendarDayModel) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val viewCircle: View = view.findViewById(R.id.viewCircle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayModel = days[position]

        // Empty cell (π.χ. αρχή μήνα)
        if (dayModel.day == null) {
            holder.tvDay.text = ""
            holder.viewCircle.visibility = View.GONE
            holder.itemView.setOnClickListener(null)
            return
        }

        // Αριθμός ημέρας
        holder.tvDay.text = dayModel.day.toString()
        holder.tvDay.setTextColor(
            ContextCompat.getColor(holder.itemView.context, R.color.calendar_day_text)
        )

        // reset
        holder.viewCircle.visibility = View.GONE

        // Χρώμα ανά DayType
        when (dayModel.type) {
            DayType.ASSIGNMENT -> {
                holder.viewCircle.setBackgroundResource(R.drawable.bg_calendar_assignment)
                holder.viewCircle.visibility = View.VISIBLE
            }
            DayType.SUBJECT -> {
                holder.viewCircle.setBackgroundResource(R.drawable.bg_calendar_subject)
                holder.viewCircle.visibility = View.VISIBLE
            }
            DayType.BOTH -> {
                holder.viewCircle.setBackgroundResource(R.drawable.bg_calendar_both)
                holder.viewCircle.visibility = View.VISIBLE
            }
            DayType.NONE -> Unit
        }

        holder.itemView.setOnClickListener {
            onDayClick(dayModel)
        }
    }

    override fun getItemCount(): Int = days.size

    fun updateData(newDays: List<CalendarDayModel>) {
        days = newDays
        notifyDataSetChanged()
    }
}
