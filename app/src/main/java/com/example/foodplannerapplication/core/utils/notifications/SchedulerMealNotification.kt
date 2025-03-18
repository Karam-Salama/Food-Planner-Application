package com.example.foodplannerapplication.core.utils.notifications
import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object SchedulerMealNotification {

    fun scheduleMealNotification(context: Context, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        // إذا كان الوقت المختار أقل من الوقت الحالي، اجعله للغد
        if (calendar.before(now)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delay = calendar.timeInMillis - now.timeInMillis

        val workRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "meal_reminder",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}