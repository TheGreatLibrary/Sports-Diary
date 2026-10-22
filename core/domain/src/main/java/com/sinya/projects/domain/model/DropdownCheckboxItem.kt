package com.sinya.projects.domain.model

data class DropdownCheckboxItem(
    val id: Int,
    val title: String,
    val checked: Boolean = false
)