package com.sinya.projects.sportsdiary.core.domain.enums

import com.sinya.projects.sportsdiary.core.domain.enums.TypeLanguages.RU

enum class TrainingMode(val mode: Int) {
    COUNT(0),
    TIME(1);

    companion object {
        fun getMode(code: Int): TrainingMode =
            TrainingMode.entries.find { it.mode == code } ?: COUNT

        fun getNegativeMode(code: Int): TrainingMode =
            when (code) {
                0 -> TIME
                else -> COUNT
            }

    }
}