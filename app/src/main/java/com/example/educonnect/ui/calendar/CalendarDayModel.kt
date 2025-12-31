package com.example.educonnect.ui.calendar

enum class DayType {
    NONE,
    ASSIGNMENT,
    SUBJECT,
    BOTH
}

data class CalendarDayModel(
    val day: Int?,
    val type: DayType
)
