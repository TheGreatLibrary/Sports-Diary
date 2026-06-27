package com.sinya.projects.sportsdiary.core.domain.enums

enum class ExerciseMode(val code: Int) {
    MAIN(2),
    SIDE(1),
    NONE(0);

    companion object {
        fun getMode(code: Int) : ExerciseMode =
            entries.find { it.code == code } ?: NONE
    }
}