package com.sinya.projects.domain.enums

enum class TrainingMode(val mode: Int) {
    COUNT(0),
    TIME(1);

    companion object {
        fun getMode(code: Int): TrainingMode =
            TrainingMode.entries.find { it.mode == code } ?: COUNT
    }
}

