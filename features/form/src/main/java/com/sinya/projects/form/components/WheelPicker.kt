package com.sinya.projects.form.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun <T> WheelPicker(
    items: List<T>,
    initialIndex: Int,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp,
    onItemSelected: (T) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex.coerceAtLeast(0))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) initialIndex
            else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: initialIndex
            }
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && centerIndex in items.indices) {
            onItemSelected(items[centerIndex])
        }
    }

    Box(
        modifier = modifier.height(itemHeight * 3),
        contentAlignment = Alignment.Center
    ) {
        // Подсветка центральной выбранной строки
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items.size) { index ->
                val isSelected = index == centerIndex
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index].toString(),
                        style = if (isSelected) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.alpha(if (isSelected) 1f else 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}




