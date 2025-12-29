package com.sinya.projects.sportsdiary.domain.model

data class SwitchItem(
    val state: Boolean,
    val onClick: (Boolean) -> Unit
)