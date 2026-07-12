package com.devwilltech.otimizacao.utils

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var showWelcomePopup: Boolean
        get() = prefs.getBoolean("show_welcome_popup", true)
        set(value) = prefs.edit().putBoolean("show_welcome_popup", value).apply()

    var savedKey: String
        get() = prefs.getString("saved_key", "") ?: ""
        set(value) = prefs.edit().putString("saved_key", value).apply()

    fun isHideStreamEnabled(): Boolean = prefs.getBoolean("hide_stream_enabled", false)
    
    fun setHideStreamEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("hide_stream_enabled", enabled).apply()
    }
}
