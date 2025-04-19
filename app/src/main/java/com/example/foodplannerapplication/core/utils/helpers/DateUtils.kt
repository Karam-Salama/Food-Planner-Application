package com.example.foodplannerapplication.core.utils.helpers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun convertDateToLong(date: String): Long {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.parse(date)?.time ?: 0L
    }

    fun convertLongToDate(timeInMillis: Long?): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timeInMillis ?: 0L))
    }

    // الدالة الجديدة للتنسيق المطلوب
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
