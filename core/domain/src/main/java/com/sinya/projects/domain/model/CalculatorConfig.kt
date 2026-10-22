package com.sinya.projects.domain.model

import androidx.annotation.StringRes

data class CalculatorConfig(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val variables: List<VariableSpec>,
    val results: List<ResultSpec>
)