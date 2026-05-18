package com.example.medicalrecordapp.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    private const val PREF_NAME = "smart_medical_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    const val LANG_AR = "ar"
    const val LANG_EN = "en"

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, LANG_AR) ?: LANG_AR
    }

    fun saveLanguage(context: Context, langCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply()
    }

    fun applyLanguage(context: Context, langCode: String): Context {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    fun wrapContext(context: Context): Context {
        val lang = getSavedLanguage(context)
        return applyLanguage(context, lang)
    }
}