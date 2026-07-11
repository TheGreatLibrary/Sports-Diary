package com.sinya.projects.sportsdiary.core.domain.enums

enum class WeightState(val state: Int) {
    MATTER(1),
    NOT_MATTER(0);

    companion object {
        fun getState(code: Int): WeightState =
            entries.find { it.state == code } ?: MATTER
    }

}