package com.example.educonnect.ui.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.Assignment
import com.example.educonnect.data.entity.Subject
import com.example.educonnect.ui.calendar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()

    private var assignments: List<Assignment> = emptyList()
    private var subjects: List<Subject> = emptyList()

    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("el"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val tvMonth = findViewById<TextView>(R.id.tvMonth)
        val recycler = findViewById<RecyclerView>(R.id.calendarRecycler)
        recycler.layoutManager = GridLayoutManager(this, 7)

        val db = AppDatabase.getDatabase(this)

        findViewById<ImageButton>(R.id.btnPrev).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            loadMonth(tvMonth, recycler)
        }

        findViewById<ImageButton>(R.id.btnNext).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            loadMonth(tvMonth, recycler)
        }

        // 🔴 Assignments (LiveData)
        db.assignmentDao().getAllAssignments().observe(this, Observer {
            assignments = it ?: emptyList()
            loadMonth(tvMonth, recycler)
        })

        // 🔵 Subjects (μια φορά)
        lifecycleScope.launch(Dispatchers.IO) {
            subjects = db.subjectDao().getAllSubjects()
            withContext(Dispatchers.Main) {
                loadMonth(tvMonth, recycler)
            }
        }
    }

    private fun loadMonth(tvMonth: TextView, recycler: RecyclerView) {
        tvMonth.text = monthFormat.format(calendar.time)

        recycler.adapter = CalendarAdapter(generateDays()) { model ->
            // ✅ εδώ το fix: παίρνουμε Int από model.day
            val dayNumber = model.day ?: return@CalendarAdapter
            openDayBottomSheet(dayNumber)
        }
    }

    private fun generateDays(): List<CalendarDayModel> {
        val list = mutableListOf<CalendarDayModel>()
        val temp = calendar.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, 1)

        val offset = (temp.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val daysInMonth = temp.getActualMaximum(Calendar.DAY_OF_MONTH)

        repeat(offset) { list.add(CalendarDayModel(null, DayType.NONE)) }

        for (day in 1..daysInMonth) {
            val hasAssignment = assignments.any { isSameDay(it.dueDate, day) }
            val hasSubject = subjects.any { it.dateMillis != null && isSameDay(it.dateMillis!!, day) }

            val type = when {
                hasAssignment && hasSubject -> DayType.BOTH
                hasAssignment -> DayType.ASSIGNMENT
                hasSubject -> DayType.SUBJECT
                else -> DayType.NONE
            }

            list.add(CalendarDayModel(day, type))
        }

        return list
    }

    private fun openDayBottomSheet(day: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day)
        }

        val events = mutableListOf<CalendarEvent>()

        assignments
            .filter { isSameDay(it.dueDate, day) }
            .forEach {
                events.add(
                    CalendarEvent.AssignmentEvent(
                        title = it.title,
                        description = it.description
                    )
                )
            }

        subjects
            .filter { it.dateMillis != null && isSameDay(it.dateMillis!!, day) }
            .forEach {
                events.add(CalendarEvent.SubjectEvent(name = it.name))
            }

        if (events.isEmpty()) return

        DayEventsBottomSheet(
            dateMillis = selectedDate.timeInMillis,
            events = events
        ).show(supportFragmentManager, "day_events")
    }

    private fun isSameDay(timestamp: Long, day: Int): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == day
    }
}
