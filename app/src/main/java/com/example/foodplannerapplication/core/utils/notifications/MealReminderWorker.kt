package com.example.foodplannerapplication.core.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.HomeActivity


class MealReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val mealTime = inputData.getLong("meal_time", 0L)
        val currentTime = System.currentTimeMillis()

        if (currentTime >= mealTime) {
            showNotification()
        }

        return Result.success()
    }

    private fun showNotification() {
        val mealTime = inputData.getLong("meal_time", 0L)
        val currentTime = System.currentTimeMillis()

        if (currentTime >= mealTime) {
            val channelId = "meal_reminder_channel"
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Meal Reminder", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("open_fragment", "weekly_plans")
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Meal Reminder")
                .setContentText("It's time for your planned meal")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1, notification)
        }
    }

}
