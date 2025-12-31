package com.example.educonnect.ui.calendar

sealed class CalendarEvent {
    data class AssignmentEvent(
        val title: String,
        val description: String
    ) : CalendarEvent()

    data class SubjectEvent(
        val name: String
    ) : CalendarEvent()
}
