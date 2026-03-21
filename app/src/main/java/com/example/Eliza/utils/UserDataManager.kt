package com.example.Eliza.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserDataManager(private val context: Context) {

    companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_BIRTHDAY = stringPreferencesKey("user_birthday")
    }

    val userName: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[USER_NAME] ?: "" }

    val userBirthday: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[USER_BIRTHDAY] ?: "" }

    suspend fun saveUserData(name: String, birthday: String) {
        context.dataStore.edit { prefs ->
            if (name.isNotBlank()) prefs[USER_NAME] = name
            if (birthday.isNotBlank()) prefs[USER_BIRTHDAY] = birthday
        }
    }
}