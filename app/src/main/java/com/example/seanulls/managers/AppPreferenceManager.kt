package com.example.seanulls.managers

import android.content.Context
import android.content.SharedPreferences

class AppPreferenceManager(context: Context, targetPreferences: String?) {
    private val preference: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        preference = context.getSharedPreferences(targetPreferences, Context.MODE_PRIVATE)
        editor = preference.edit()
    }

    fun containChecker() {
        if (!preference.contains(THEME_NUMBER)) editor.putInt(THEME_NUMBER, 0).commit()
    }

    companion object {
        const val SPRITES = "spritesPreferences"
        private const val THEME_NUMBER = "themeNumber"
    }
}