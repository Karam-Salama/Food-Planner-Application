package com.example.foodplannerapplication.core.helpers

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.util.*

object CalendarHelper {
    fun addMealToCalendar(
        context: Context,
        mealName: String,
        mealDescription: String,
        startTimeMillis: Long,
        endTimeMillis: Long,
        reminderMinutes: Int = 15
    ): Long? {
        return try {
            val contentResolver = context.contentResolver
            val calendarId = getDefaultCalendarId(contentResolver) ?: return null

            val event = ContentValues().apply {
                put(CalendarContract.Events.TITLE, mealName)
                put(CalendarContract.Events.DESCRIPTION, mealDescription)
                put(CalendarContract.Events.DTSTART, startTimeMillis)
                put(CalendarContract.Events.DTEND, endTimeMillis)
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.HAS_ALARM, 1)
            }

            val eventUri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, event)
            eventUri?.let { uri ->
                val eventId = ContentUris.parseId(uri)
                val reminder = ContentValues().apply {
                    put(CalendarContract.Reminders.MINUTES, reminderMinutes)
                    put(CalendarContract.Reminders.EVENT_ID, eventId)
                    put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                }
                contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminder)
                eventId
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getDefaultCalendarId(contentResolver: ContentResolver): Long? {
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.IS_PRIMARY} = ?"
        val selectionArgs = arrayOf("1")

        return contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) else null
        }
    }
}