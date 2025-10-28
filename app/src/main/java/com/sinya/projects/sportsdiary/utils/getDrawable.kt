package com.sinya.projects.sportsdiary.utils

import android.content.Context
import com.sinya.projects.sportsdiary.R

fun Context.getDrawableId(nameKey: String): Int {
    val id = resources.getIdentifier(nameKey, "drawable", packageName)
    return if (id != 0) id else R.drawable.ic_launcher_foreground
}