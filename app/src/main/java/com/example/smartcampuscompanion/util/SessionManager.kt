package com.example.smartcampuscompanion.util

import android.content.Context

object SessionManager {

    private const val PREF_NAME   = "user_session"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USERNAME  = "username"
    private const val KEY_ROLE      = "role"  // NEW

    // role: "student" or "admin"
    fun saveLogin(context: Context, username: String, role: String = "student") {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_USERNAME, username)
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOGGED_IN, false)

    fun getUsername(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USERNAME, "Student") ?: "Student"

    fun getRole(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ROLE, "student") ?: "student"

    fun isAdmin(context: Context): Boolean = getRole(context) == "admin"

    fun logout(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}
