package com.sinya.projects.sportsdiary.presentation.trainingPage.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.sinya.projects.sportsdiary.domain.model.ExerciseItem
import com.sinya.projects.sportsdiary.domain.model.ReorderState

@Composable
fun <T> DragOverlay(
    reorderState: ReorderState<T>,
    items: List<ExerciseItem>,
    listState: LazyListState,
) {
    val item = items.find { it.id == reorderState.draggedItemId } ?: return
    val currentIndex = items.indexOfFirst { it.id == reorderState.draggedItemId }

    if (currentIndex >= 0) {
        val layoutInfo = remember { derivedStateOf { listState.layoutInfo } }
        val itemInfo = layoutInfo.value.visibleItemsInfo
            .firstOrNull { it.index == currentIndex + 2 } ?: return
        
        Popup(
            offset = IntOffset(0, itemInfo.offset + reorderState.dragOffset.toInt()),
            properties = PopupProperties(
                focusable = false,
                clippingEnabled = false
            )
        ) {
            SwipeTrainingCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp),
                item = item,
                onInfoClick = {},
                onPlusClick = {},
                onMinusClick = {},
                onEditSet = { _, _, _, _ -> },
                onDeleteSet = { _, _ -> }
            )
        }
    }
}

