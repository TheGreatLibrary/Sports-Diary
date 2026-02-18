package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow


@Composable
fun rememberScrollDirection(
    listState: LazyListState,
    threshold: Int = 50
): Boolean {
    var showHeader by remember { mutableStateOf(true) }
    var totalScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex * 1000 + listState.firstVisibleItemScrollOffset
        }.collect { currentTotal ->

            if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 100) {
                showHeader = true
            } else {
                val delta = currentTotal - totalScrollOffset

                if (delta > threshold) {
                    showHeader = false
                }
                else if (delta < -threshold) {
                    showHeader = true
                }
            }

            totalScrollOffset = currentTotal
        }
    }

    return showHeader
}