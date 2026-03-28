package com.example.Eliza

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
            val bgColor = MoodColorCalculator.getColor(balance)
            val textColor = getContrastColor(bgColor)
            val message = getMessageForBalance(balance)

            val views = RemoteViews(context.packageName, R.layout.widget_mood)
            views.setTextViewText(R.id.tvMoodNumber, if (balance > 0) "+$balance" else balance.toString())
            views.setTextViewText(R.id.tvMoodMessage, message)
            views.setTextColor(R.id.tvMoodNumber, textColor)
            views.setTextColor(R.id.tvMoodMessage, textColor)
            views.setInt(R.id.widgetRoot, "setBackgroundColor", bgColor)

            // Клик по виджету открывает приложение
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getContrastColor(color: Int): Int {
            // Вычисляем яркость по формуле (0.299*R + 0.587*G + 0.114*B)
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            val brightness = (0.299 * red + 0.587 * green + 0.114 * blue) / 255

            return if (brightness > 0.5) Color.BLACK else Color.WHITE
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