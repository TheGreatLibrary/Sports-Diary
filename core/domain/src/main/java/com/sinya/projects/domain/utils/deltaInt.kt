package com.sinya.projects.domain.utils

fun deltaInt(cur: String?, prev: String?): Int? {
    val c = safeInt(cur) ?: return null
    val p = safeInt(prev) ?: return null
    return c - p
}
