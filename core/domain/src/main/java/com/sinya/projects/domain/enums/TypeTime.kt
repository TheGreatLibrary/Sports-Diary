package com.sinya.projects.domain.enums

enum class TypeTime(val index: Int) {
    DAYS(0),
    MONTHS(1),
    YEARS(2);

    companion object {
        fun fromIndex(index: Int): TypeTime =
            entries.find { it.index == index } ?: DAYS
    }
}