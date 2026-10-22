package com.sinya.projects.domain.model

data class SwitchItem(
    val state: Boolean,
    val onClick: (Boolean) -> Unit
)