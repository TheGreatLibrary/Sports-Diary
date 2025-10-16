package com.sinya.projects.sportsdiary.ui.features

import android.content.Context

fun Context.getString(nameKey: String): String {
    val id = resources.getIdentifier(nameKey, "string", packageName)
    return if (id != 0) getString(id) else nameKey
}