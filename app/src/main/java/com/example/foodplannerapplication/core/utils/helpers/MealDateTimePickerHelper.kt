package com.example.foodplannerapplication.core.utils.helpers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.EditText

class MealDateTimePickerHelper(private val context: Context) {

    @SuppressLint("SetTextI18n")
    fun showDatePicker(editText: EditText?) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            editText?.setText("$day/${month + 1}/$year")
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            val selectedDate = "$day/${month + 1}/$year"
            onDateSelected(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    @SuppressLint("DefaultLocale")
    fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(context, { _, hour, minute ->
            editText.setText(String.format("%02d:%02d", hour, minute))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }
}