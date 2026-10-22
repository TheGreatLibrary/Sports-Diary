package com.sinya.projects.domain.model

import androidx.annotation.StringRes

data class VariableSpec(
    val key: String,
    @StringRes val titleRes: Int,
    @StringRes val measureRes: Int,
    val defaultValue: String = "10"
)