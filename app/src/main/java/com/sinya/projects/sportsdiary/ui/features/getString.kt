package com.sinya.projects.sportsdiary.ui.features

import android.content.Context
import android.util.Log
import com.sinya.projects.sportsdiary.R

fun Context.getString(nameKey: String): String {
    val id = resources.getIdentifier(nameKey, "string", packageName)
    return if (id != 0) getString(id) else nameKey
}

fun Context.getId(nameKey: String): Int {
    val id = resources.getIdentifier(nameKey, "string", packageName)
    Log.d("DDD", nameKey+id.toString())
    return if (id!=0) id else R.string.minus
}

fun Context.getDrawableId(nameKey: String): Int {
    val id = resources.getIdentifier(nameKey, "drawable", packageName)
    return if (id != 0) id else R.drawable.ic_launcher_foreground
}