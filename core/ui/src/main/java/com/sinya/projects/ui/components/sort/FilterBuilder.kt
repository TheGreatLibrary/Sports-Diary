package com.sinya.projects.ui.components.sort

import androidx.compose.foundation.shape.CornerBasedShape
import com.sinya.projects.domain.model.RadioItem
import com.sinya.projects.ui.components.sort.SortParam

data class FilterBuilder<R>(
    val titleRes: Int,
    val options: List<RadioItem<R>>,
    val selectedValue: R,
    val onSelect: (R) -> SortParam,
    val shape: CornerBasedShape
)