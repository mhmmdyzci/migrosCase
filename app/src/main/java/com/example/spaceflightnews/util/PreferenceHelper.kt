package com.example.spaceflightnews.util

import android.content.Context
import androidx.core.content.edit

object PreferenceHelper {
    private const val PREF_NAME = "spaceflight_prefs"
    private const val KEY_LAST_UPDATE = "last_update_time"

    fun saveLastUpdateTime(context: Context, timestamp: Long) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { putLong(KEY_LAST_UPDATE, timestamp) }
    }

    fun getLastUpdateTime(context: Context): Long {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_UPDATE, -1)
    }
}