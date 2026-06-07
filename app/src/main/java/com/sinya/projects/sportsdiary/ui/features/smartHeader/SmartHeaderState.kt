package com.sinya.projects.sportsdiary.ui.features.smartHeader

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection

data class SmartHeaderState(
    val canScroll: Boolean = false,
    val headerHeightPx: Float = 0f,
    val headerOffsetPx: Float = 0f,
    val listState: LazyListState,
    val headerScrollConnection: NestedScrollConnection,
)