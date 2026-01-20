package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sinya.projects.sportsdiary.domain.model.ReorderState
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