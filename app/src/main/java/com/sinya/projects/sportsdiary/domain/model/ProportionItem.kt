package com.sinya.projects.sportsdiary.domain.model

data class ProportionItem(
    val id: Int?,
    val title: String,
    val date: String,
    val items: List<ProportionRow> = emptyList(),
)