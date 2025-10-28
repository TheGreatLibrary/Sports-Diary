package com.sinya.projects.sportsdiary.utils

fun trimZeros(f: Float): String = if (f % 1f == 0f) f.toInt().toString() else f.toString()
