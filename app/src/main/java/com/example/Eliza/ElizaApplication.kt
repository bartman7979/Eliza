package com.example.Eliza

import android.app.Application
import androidx.work.*
import java.util.concurrent.TimeUnit

class ElizaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleNotifications()
    }

    private fun scheduleNotifications() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.HOURS // периодичность
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS) // начать через час после запуска
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "eliza_notifications",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}