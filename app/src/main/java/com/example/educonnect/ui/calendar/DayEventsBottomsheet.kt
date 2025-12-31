package com.example.educonnect.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DayEventsBottomSheet(
    private val dateMillis: Long,
    private val events: List<CalendarEvent>
) : BottomSheetDialogFragment() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottomsheet_day_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerEvents)

        tvTitle.text = "Events • ${dateFormat.format(Date(dateMillis))}"

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = DayAssignmentsAdapter(events)
    }
}
