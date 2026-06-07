package com.sinya.projects.sportsdiary.core.domain.model

import com.sinya.projects.sportsdiary.core.domain.enums.TypeLanguages

data class UiConfig(
    val dark: Boolean = true,
    val language: String = TypeLanguages.RU.code,
    val trainingWarning: Boolean = true,
    val planMorningId: Int? = null
)