package com.sinya.projects.sportsdiary.core.domain.model

data class BottomSheetCategoryData(
    val categoryName: String = "",
    val query: String = "",
    val isError: Boolean = false
)