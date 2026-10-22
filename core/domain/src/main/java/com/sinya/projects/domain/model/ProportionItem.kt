package com.sinya.projects.domain.model

data class ProportionItem(
    val id: Int?,
    val title: String,
    val date: String,
    val items: List<ProportionRow> = emptyList(),
)