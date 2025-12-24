package com.example.educonnect.ui.activities

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.educonnect.R

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(this, "Selected: $date", Toast.LENGTH_SHORT).show()
        }
    }
}
