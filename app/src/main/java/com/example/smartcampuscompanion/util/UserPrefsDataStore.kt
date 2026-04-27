package com.example.smartcampuscompanion.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPrefsDataStore {

    private val KEY_DARK_MODE     = booleanPreferencesKey("dark_mode")
    private val KEY_NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")

    fun observeDarkMode(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_DARK_MODE] ?: false }

    fun observeNotifications(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_NOTIF_ENABLED] ?: true }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    suspend fun setNotifications(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIF_ENABLED] = enabled }
    }
}
