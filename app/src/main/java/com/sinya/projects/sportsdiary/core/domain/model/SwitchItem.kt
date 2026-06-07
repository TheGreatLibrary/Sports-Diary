package com.sinya.projects.sportsdiary.core.domain.model

data class SwitchItem(
    val state: Boolean,
    val onClick: (Boolean) -> Unit
)