package com.sinya.projects.sportsdiary.domain.model

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
class ReorderState<T>(
    private val listState: LazyListState,
    private val onMove: (Int, Int) -> Unit,
    private val scope: CoroutineScope,
    private val idProvider: (T) -> Int
) {
    var draggedItemId by mutableStateOf<Int?>(null)
    var dragOffset by mutableFloatStateOf(0f)

    var items by mutableStateOf<List<T>>(emptyList())


    private val autoScrollEdgePx = 120f
    private val maxAutoScrollSpeed = 20f
    private var autoScrollJob: Job? = null

    fun onDragStart(itemId: Int) {
        draggedItemId = itemId
        dragOffset = 0f
    }

    private fun startAutoScroll(speed: Float) {
        autoScrollJob = scope.launch {
            while (true) {
                listState.scrollBy(speed)
                delay(16L) // ~60fps
            }
        }
    }

    fun onDrag(dragAmount: Float) {
        dragOffset += dragAmount

        val draggedId = draggedItemId ?: return
        val currentIndex = items.indexOfFirst { idProvider(it) == draggedId }
        if (currentIndex < 0) return

        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo
        val currentItem = visibleItems.find { it.index == currentIndex + 2 } ?: return

        val draggedTop = currentItem.offset + dragOffset
        val draggedBottom = draggedTop + currentItem.size

        val viewportStart = layoutInfo.viewportStartOffset
        val viewportEnd = layoutInfo.viewportEndOffset

        autoScrollJob?.cancel()
        when {
            draggedBottom > viewportEnd - autoScrollEdgePx -> {
                val factor =
                    ((draggedBottom - (viewportEnd - autoScrollEdgePx)) / autoScrollEdgePx).coerceIn(
                        0f,
                        1f
                    )
                val speed = factor * maxAutoScrollSpeed
                startAutoScroll(speed)
            }

            draggedTop < viewportStart + autoScrollEdgePx -> {
                val factor =
                    ((viewportStart + autoScrollEdgePx - draggedTop) / autoScrollEdgePx).coerceIn(
                        0f,
                        1f
                    )
                val speed = -factor * maxAutoScrollSpeed
                startAutoScroll(speed)
            }
        }

        val triggerY = draggedTop + currentItem.size

        val target = visibleItems.firstOrNull {
            it.index >= 2 &&
                    triggerY in (it.offset + it.size - 5).toFloat()..(it.offset + it.size + 5).toFloat()
        }
            ?: return

        val targetIndex = target.index - 2

        if (targetIndex != currentIndex && targetIndex >= 0 && targetIndex < items.size) {
            onMove(currentIndex, targetIndex)
            dragOffset = 0f // Сбрасываем offset после перемещения
        }
    }

    fun onDragEnd() {
        draggedItemId = null
        dragOffset = 0f
        autoScrollJob?.cancel()
        autoScrollJob = null
    }
}