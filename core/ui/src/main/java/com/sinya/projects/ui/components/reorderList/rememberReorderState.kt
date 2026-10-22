package com.sinya.projects.ui.components.reorderList

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope

@Composable
fun <T> rememberReorderState(
    listState: LazyListState,
    items: List<T>,
    scope: CoroutineScope,
    idProvided: (T) -> Int,
    onMove: (Int, Int) -> Unit
): ReorderState<T> = remember { ReorderState(listState, onMove, scope, idProvided) }.apply {
    this.items = items
}