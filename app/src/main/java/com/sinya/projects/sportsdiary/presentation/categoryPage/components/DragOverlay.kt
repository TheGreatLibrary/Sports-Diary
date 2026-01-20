package com.sinya.projects.sportsdiary.presentation.categoryPage.components

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
import com.sinya.projects.sportsdiary.domain.model.ExerciseItemWithoutList
import com.sinya.projects.sportsdiary.domain.model.ReorderState
import com.sinya.projects.sportsdiary.ui.features.SwipeCard


@Composable
fun <T> DragOverlay(
    reorderState: ReorderState<T>,
    items: List<ExerciseItemWithoutList>,
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
            SwipeCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp),
                id = item.id,
                title = item.title,
                onTrainingClick = {  },
                onDelete = {  }
            )
        }
    }
}
