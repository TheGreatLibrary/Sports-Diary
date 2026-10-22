package com.sinya.projects.domain.model

import androidx.annotation.StringRes

data class ResultSpec(
    @StringRes val titleRes: Int,
    @StringRes val measureRes: Int,
    val formula: (Map<String, Double>) -> Double
)