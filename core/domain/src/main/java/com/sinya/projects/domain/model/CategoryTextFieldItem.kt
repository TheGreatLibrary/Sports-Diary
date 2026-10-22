package com.sinya.projects.domain.model

data class CategoryTextFieldItem(
    val category: String,
    val onCategoryChange: (String) -> Unit,
    val isError: Boolean = false,
)