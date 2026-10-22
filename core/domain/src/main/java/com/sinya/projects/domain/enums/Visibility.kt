package com.sinya.projects.domain.enums

enum class Visibility(val state: Boolean) {
    VISIBLE(true),
    INVISIBLE(false);

    companion object {
        fun getState(code: Boolean): Visibility =
            entries.find { it.state == code } ?: VISIBLE
    }
}