// NotificationWorker.kt
package com.example.Eliza

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.Eliza.utils.PersonalityManager
import com.example.Eliza.utils.PersonalityType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // --- проверка времени ---
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (currentHour !in 8..20) {
            return Result.success() // не показываем уведомление, выходим
        }
        // Получаем выбранную личность
        val personalityManager = PersonalityManager(applicationContext)
        val personality = personalityManager.currentPersonality.first()
        // Получаем массив фраз в зависимости от личности
        val phrases = getPhrases(personality)

        if (phrases.isNotEmpty()) {
            val randomPhrase = phrases[Random.nextInt(phrases.size)]
            showNotification(randomPhrase)
        }

        return Result.success()
    }

    private fun getPhrases(personality: PersonalityType): Array<String> {
        return when (personality) {
            PersonalityType.FRIEND -> applicationContext.resources.getStringArray(R.array.encouragements_friend)
            PersonalityType.SISTER -> applicationContext.resources.getStringArray(R.array.encouragements_sister)
            PersonalityType.COACH -> applicationContext.resources.getStringArray(R.array.encouragements_coach)
        }
    }

    private fun showNotification(message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаём канал для уведомлений (для Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "eliza_channel",
                "Элиза",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления от Элизы"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "eliza_channel")
            .setContentTitle("Элиза")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // используйте свою иконку
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}