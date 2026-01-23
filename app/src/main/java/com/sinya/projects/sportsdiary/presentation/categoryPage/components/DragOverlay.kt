package com.sinya.projects.sportsdiary.presentation.categoryPage.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.sinya.projects.sportsdiary.domain.model.ReorderState

@Composable
fun DragOverlay(
    reorderState: ReorderState<*>,
    index: Int,
    listState: LazyListState,
    overlayContent: @Composable () -> Unit
) {
    if (index < 0) return

    val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index + 2 }
    if (itemInfo == null) return

    Popup(
        offset = IntOffset(0, itemInfo.offset + reorderState.dragOffset.toInt()),
        properties = PopupProperties(focusable = false, clippingEnabled = false)
    ) {
        overlayContent()
    }
}