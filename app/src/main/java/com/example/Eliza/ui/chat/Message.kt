package com.example.Eliza.ui.chat

data class Message(
    val text: String,
    val isUser: Boolean, // true – от пользователя, false – от Элизы
    val timestamp: Long = System.currentTimeMillis()
)