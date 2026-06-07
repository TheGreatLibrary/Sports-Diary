package com.sinya.projects.sportsdiary.core.domain.enums

enum class TypeLanguages(val code: String) {
    RU("ru"),
    EN("en");

    companion object {
        fun fromCode(code: String): TypeLanguages =
            entries.find { it.code == code } ?: RU
    }
}