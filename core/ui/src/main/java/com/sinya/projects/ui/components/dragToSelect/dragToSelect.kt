package com.sinya.projects.ui.components.dragToSelect

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


private class DragSelectState {
    var currentY: Float = 0f
    var currentHoverId: Int? = null
}

fun Modifier.dragToSelect(
    lazyListState: LazyListState,
    onStartSelection: (Int) -> Unit,
    onHoverItem: (Int) -> Unit
): Modifier = composed {
    var autoScrollSpeed by remember { mutableFloatStateOf(0f) }
    val dragState = remember { DragSelectState() }

    val checkSelection: () -> Unit = {
        val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
        val hoveredItem = visibleItems.find {
            dragState.currentY >= it.offset && dragState.currentY <= (it.offset + it.size)
        }

        val key = hoveredItem?.key as? Int
        if (key != null && key != dragState.currentHoverId) {
            dragState.currentHoverId = key
            onHoverItem(key)
        }
    }

    LaunchedEffect(autoScrollSpeed) {
        if (autoScrollSpeed != 0f) {
            while (isActive) {
                lazyListState.scrollBy(autoScrollSpeed)
                checkSelection()
                delay(16)
            }
        }
    }

    this.pointerInput(Unit) {
        val listHeight = size.height
        val scrollThreshold = listHeight * 0.15f
        val maxScrollSpeed = 25f

        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                dragState.currentY = offset.y
                val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
                // Находим элемент, на который долго нажали
                val hoveredItem = visibleItems.find {
                    offset.y >= it.offset && offset.y <= (it.offset + it.size)
                }

                val key = hoveredItem?.key as? Int
                if (key != null) {
                    dragState.currentHoverId = key
                    onStartSelection(key)
                }
            },
            onDrag = { change, _ ->
                change.consume() // Поглощаем жест только когда потянули после долгого нажатия
                val y = change.position.y
                dragState.currentY = y

                // Авто-скролл
                if (y < scrollThreshold) {
                    val ratio = 1f - (y / scrollThreshold).coerceIn(0f, 1f)
                    autoScrollSpeed = -(maxScrollSpeed * ratio)
                } else if (y > listHeight - scrollThreshold) {
                    val distanceToBottom = listHeight - y
                    val ratio = 1f - (distanceToBottom / scrollThreshold).coerceIn(0f, 1f)
                    autoScrollSpeed = (maxScrollSpeed * ratio)
                } else {
                    autoScrollSpeed = 0f
                }

                checkSelection()
            },
            onDragEnd = {
                autoScrollSpeed = 0f
                dragState.currentHoverId = null
            },
            onDragCancel = {
                autoScrollSpeed = 0f
                dragState.currentHoverId = null
            }
        )
    }
}