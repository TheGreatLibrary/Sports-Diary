package com.sinya.projects.sportsdiary.ui.features.smartHeader

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SmartHeaderManager {
    private val _headerHeightPx = MutableStateFlow(0f)
    val headerHeightPx = _headerHeightPx.asStateFlow()

    private val _headerOffsetPx = MutableStateFlow(0f)
    val headerOffsetPx = _headerOffsetPx.asStateFlow()

    private val _canScroll = MutableStateFlow(true)

    @SuppressLint("FrequentlyChangingValue")
    @Composable
    fun rememberScrollDirection(): SmartHeaderState {
        val listState = rememberLazyListState()

        val canScrollForward by remember {
            derivedStateOf {
                listState.canScrollForward
            }
        }

        LaunchedEffect(canScrollForward) {
            _canScroll.update { canScrollForward }
        }

        val headerScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val currentOffset = _headerOffsetPx.value
                    val maxOffset = 0f
                    val minOffset = -_headerHeightPx.value

                    if (!canScrollForward && delta < 0) return Offset.Zero

                    val newOffset = (currentOffset + delta).coerceIn(minOffset, maxOffset)
                    val consumed = newOffset - currentOffset

                    if (_headerHeightPx.value > 0f && consumed != 0f) {
                        _headerOffsetPx.update { newOffset }
                        return Offset(0f, consumed)
                    }

                    return Offset.Zero
                }
            }
        }

        LaunchedEffect(listState) {
            snapshotFlow {
                listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
            }.collect { (index, offset) ->
                if (index == 0 && offset == 0 && _headerOffsetPx.value != 0f) {
                    _headerOffsetPx.update { 0f }
                }
            }
        }

        return SmartHeaderState(
            canScroll = canScrollForward,
            headerOffsetPx = _headerOffsetPx.collectAsStateWithLifecycle().value,
            headerHeightPx = _headerHeightPx.collectAsStateWithLifecycle().value,
            listState = listState,
            headerScrollConnection = headerScrollConnection
        )
    }

    fun setHeight(value: Float) {
        _headerHeightPx.update { value }

    }

    fun setOffset(value: Float) {
        _headerOffsetPx.update { value }
    }

    fun reset() {
        _headerOffsetPx.update { 0f }
        _headerHeightPx.update { 0f }
    }
}