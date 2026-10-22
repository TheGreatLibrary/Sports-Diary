package com.sinya.projects.domain.model

data class PopupItem<T>(
    val title: String,
    val icon: Int? = null,
    val onClick: (T) -> Unit
)