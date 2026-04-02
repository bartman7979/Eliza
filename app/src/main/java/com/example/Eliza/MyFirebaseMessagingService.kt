// MyFirebaseMessagingService.kt
package com.example.Eliza

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // Можно сохранить token в SharedPreferences, если позже понадобится
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Если вы решите отправлять через сервер, здесь будет обработка
        // Для локального планирования этот метод не нужен
    }
}