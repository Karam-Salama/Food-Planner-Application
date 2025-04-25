package com.example.foodplannerapplication.core.helpers
import java.util.Calendar

object DateTimeValidator {
    fun validateDateTimeInFuture(dateTimeInMillis: Long?): Pair<Boolean, String?> {
        return when {
            dateTimeInMillis == null -> Pair(false, "Date/time cannot be null")
            dateTimeInMillis <= 0 -> Pair(false, "Invalid date/time value")
            else -> Pair(dateTimeInMillis > Calendar.getInstance().timeInMillis, "Date/time must be in the future")
        }
    }
}