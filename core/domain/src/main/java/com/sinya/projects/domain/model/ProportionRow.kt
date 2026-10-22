package com.sinya.projects.domain.model

data class ProportionRow(
    val id: Int,
    val title: String,
    val value: String,
    val unitMeasure: String,
    val delta: String? = null
)