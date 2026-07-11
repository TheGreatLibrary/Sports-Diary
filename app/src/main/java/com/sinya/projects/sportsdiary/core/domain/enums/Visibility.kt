package com.sinya.projects.sportsdiary.core.domain.enums

enum class Visibility(val state: Int) {
    VISIBLE(1),
    INVISIBLE(0);

    companion object {
        fun getState(code: Int): Visibility =
            entries.find { it.state == code } ?: VISIBLE
    }
}