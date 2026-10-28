package com.sinya.projects.domain.model

import com.sinya.projects.domain.enums.TypeLanguages

data class UiConfig(
    val dark: Boolean = true,
    val map7: Boolean = true,
    val language: String = TypeLanguages.RU.code,
    val trainingWarning: Boolean = true,
    val planMorningId: Int? = null
)