package com.example.foodplannerapplication.core.utils.helpers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.EditText
import java.util.Date

class MealDateTimePickerHelper(private val context: Context) {

    fun showFullDateTimePicker(
        onDateTimeSelected: (Long) -> Unit,
        onTimePickerShown: () -> Unit = {},
        onDatePickerShown: () -> Unit = {}
    ) {
        showDatePickerCalendar(
            onDateSelected = { selectedDate ->
                onDatePickerShown()
                showTimePickerWithAmPm(
                    onTimeSelected = { hour12, minute, isPm ->
                        val calendar = Calendar.getInstance().apply {
                            time = selectedDate
                            set(Calendar.HOUR_OF_DAY, convertTo24Hour(hour12, isPm))
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                        }
                        onDateTimeSelected(calendar.timeInMillis)
                    },
                    onTimePickerShown = onTimePickerShown
                )
            },
            onDatePickerShown = onDatePickerShown
        )
    }

    private fun convertTo24Hour(hour12: Int, isPm: Boolean): Int {
        return when {
            isPm && hour12 < 12 -> hour12 + 12
            !isPm && hour12 == 12 -> 0
            else -> hour12
        }
    }

    private fun showDatePickerCalendar(
        onDateSelected: (Date) -> Unit,
        onDatePickerShown: () -> Unit = {}
    ) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }.time
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.setOnShowListener { onDatePickerShown() }
        dialog.show()
    }

    @SuppressLint("DefaultLocale")
    private fun showTimePickerWithAmPm(
        onTimeSelected: (hour12: Int, minute: Int, isPm: Boolean) -> Unit,
        onTimePickerShown: () -> Unit = {}
    ) {
        val calendar = Calendar.getInstance()
        var isPm = calendar.get(Calendar.AM_PM) == Calendar.PM

        val dialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val hour12 = convertTo12Hour(hourOfDay)
                isPm = hourOfDay >= 12
                onTimeSelected(hour12, minute, isPm)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // استخدام نظام 24 ساعة داخلياً
        )
        dialog.setOnShowListener { onTimePickerShown() }
        dialog.show()
    }

    private fun convertTo12Hour(hour24: Int): Int {
        return when (hour24) {
            0 -> 12
            in 1..12 -> hour24
            else -> hour24 - 12
        }
    }

    // باقي الدوال الحالية للتوافق مع الأكواد القديمة
    @SuppressLint("SetTextI18n")
    fun showDatePicker(editText: EditText) {
        showDatePickerString { dateString ->
            editText.setText(dateString)
        }
    }

    fun showDatePickerString(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            val selectedDate = "$day/${month + 1}/$year"
            onDateSelected(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


}