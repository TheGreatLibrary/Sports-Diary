package com.sinya.projects.sportsdiary.core.utils

fun trimZeros(f: Float): String = if (f % 1f == 0f) f.toInt().toString() else f.toString()
