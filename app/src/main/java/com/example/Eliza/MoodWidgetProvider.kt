package com.example.Eliza

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.Eliza.utils.MoodColorCalculator

class MoodWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            val balance = prefs.getInt("balance", 0)
            val color = MoodColorCalculator.getColor(balance)
            val message = getMessageForBalance(balance)

            val views = RemoteViews(context.packageName, R.layout.widget_mood)
            views.setTextViewText(R.id.tvMoodNumber, if (balance > 0) "+$balance" else balance.toString())
            views.setTextViewText(R.id.tvMoodMessage, message)
            views.setTextColor(R.id.tvMoodNumber, color)
            views.setTextColor(R.id.tvMoodMessage, color)

            // Клик по виджету открывает приложение
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.rootLayout, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getMessageForBalance(balance: Int): String {
            return when {
                balance <= -70 -> "Держись 💪"
                balance <= -40 -> "Ты справишься ✨"
                balance <= -10 -> "Передышка 🌸"
                balance <= 9 -> "Нормально 🌿"
                balance <= 39 -> "Хорошо 🌞"
                balance <= 69 -> "Отлично 🎉"
                else -> "Супер! 🌟"
            }
        }
    }
}