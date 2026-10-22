package com.sinya.projects.domain.enums

enum class TypeLanguages(val code: String) {
    RU("ru"),
    EN("en");

    companion object {
        fun fromCode(code: String): TypeLanguages =
            entries.find { it.code == code } ?: RU
    }
}