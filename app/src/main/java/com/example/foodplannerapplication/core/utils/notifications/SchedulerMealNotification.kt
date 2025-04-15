package com.example.foodplannerapplication.core.utils.notifications
import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object SchedulerMealNotification {
    fun scheduleMealNotification(
        context: Context,
        notificationTimeInMillis: Long, // تغيير من Int إلى Long
        mealName: String
    ) {
        val currentTime = System.currentTimeMillis()

        if (notificationTimeInMillis <= currentTime) {
            return
        }

        val delay = notificationTimeInMillis - currentTime

        val inputData = workDataOf(
            "meal_name" to mealName
        )

        val workRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "meal_reminder_${notificationTimeInMillis}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}