package com.example.foodplannerapplication.core.helpers
import java.util.Calendar

object DateTimeValidator {
    /**
     * Validates if the given date-time is in the future
     * @return Pair where first is isValid (Boolean) and second is error message (String?)
     */
    fun validateDateTimeInFuture(dateTimeInMillis: Long?): Pair<Boolean, String?> {
        return when {
            dateTimeInMillis == null -> Pair(false, "Date/time cannot be null")
            dateTimeInMillis <= 0 -> Pair(false, "Invalid date/time value")
            else -> Pair(dateTimeInMillis > Calendar.getInstance().timeInMillis, "Date/time must be in the future")
        }
    }

    /**
     * Validates if the given date is today or in the future (ignores time)
     */
    fun validateDateInFuture(dateInMillis: Long?): Pair<Boolean, String?> {
        if (dateInMillis == null) return Pair(false, "Date cannot be null")

        val currentDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return Pair(dateInMillis >= currentDate, "Date must be today or in the future")
    }

    /**
     * Validates if the given time is in the future (compared to current time)
     */
    fun validateTimeInFuture(timeInMillis: Long?): Pair<Boolean, String?> {
        if (timeInMillis == null) return Pair(false, "Time cannot be null")
        return Pair(timeInMillis > Calendar.getInstance().timeInMillis, "Time must be in the future")
    }
}