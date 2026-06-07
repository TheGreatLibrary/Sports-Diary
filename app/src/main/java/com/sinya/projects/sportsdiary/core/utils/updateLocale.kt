package com.sinya.projects.sportsdiary.core.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun Context.updateLocale(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    configuration.setLayoutDirection(locale)
    @Suppress("DEPRECATION")
    resources.updateConfiguration(configuration, resources.displayMetrics)
}