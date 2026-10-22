package com.sinya.projects.domain.model

data class CardItem(
    val title: String,
    val icon: Int,
    val onClick: () -> Unit,
    val description: String? = null,
    val switchState: Boolean? = null
)

