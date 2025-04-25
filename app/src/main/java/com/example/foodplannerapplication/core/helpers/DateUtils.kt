package com.example.foodplannerapplication.core.helpers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun convertLongToFormattedDateTime(timeInMillis: Long?): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
        return sdf.format(Date(timeInMillis ?: 0L))
    }

    fun formatDate(millis: Long): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
    }

    fun formatTime(millis: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(millis))
    }

    fun convertDateTimeToLong(dateTimeStr: String): Long {
        return try {
            SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).parse(dateTimeStr)?.time ?: 0
        } catch (e: Exception) {
            0
        }
    }
}
