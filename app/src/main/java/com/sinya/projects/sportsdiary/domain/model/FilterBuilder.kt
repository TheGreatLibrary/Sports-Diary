package com.sinya.projects.sportsdiary.domain.model

import androidx.compose.ui.graphics.Shape

data class FilterBuilder<R>(
    val titleRes: Int,
    val options: List<RadioItem<R>>,
    val selectedValue: R,
    val onSelect: (R) -> SortParam,
    val shape: Shape
)