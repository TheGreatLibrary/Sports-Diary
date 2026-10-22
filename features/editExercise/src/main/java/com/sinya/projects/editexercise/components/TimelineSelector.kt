package com.sinya.projects.editexercise.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
//
//@Composable
//fun <T> TimelineSelector(
//    items: List<T>,
//    selectedItem: T?,
//    nameSelector: (T) -> String,
//    onItemSelected: (T) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth(),
//        contentAlignment = Alignment.Center
//    ) {
//        HorizontalDivider(modifier = Modifier.fillMaxWidth().background(Color.White).height(1.dp))
//        LazyRow(
//            modifier = modifier.fillMaxWidth(),
//            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//            horizontalArrangement = Arrangement.spacedBy(0.dp) // 0.dp важно для неразрывной линии
//        ) {
//            itemsIndexed(items) { index, item ->
//                TimelineNode(
//                    text = nameSelector(item),
//                    isSelected = item == selectedItem,
//                    isFirst = index == 0,
//                    isLast = index == items.lastIndex,
//                    isTop = index % 2 == 0, // Чередование: четные сверху, нечетные снизу
//                    onClick = { onItemSelected(item) }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun TimelineNode(
//    text: String,
//    isSelected: Boolean,
//    isFirst: Boolean,
//    isLast: Boolean,
//    isTop: Boolean,
//    onClick: () -> Unit
//) {
//    // Цвета можно заменить на кастомные из твоей темы
//    val lineColor = MaterialTheme.colorScheme.outlineVariant
//    val selectedColor = MaterialTheme.colorScheme.primary
//    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
//
//    Column(
//        modifier = Modifier
//            .widthIn(min = 90.dp) // Минимальная ширина для комфортного тапа и длины линии
//            .clickable(onClick = onClick),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Контейнер для верхнего текста
//        Box(
//            modifier = Modifier
//                .height(40.dp)
//                .padding(horizontal = 4.dp),
//            contentAlignment = Alignment.BottomCenter
//        ) {
//            if (isTop) {
//                Text(
//                    text = text,
//                    style = MaterialTheme.typography.labelMedium,
//                    color = if (isSelected) selectedColor else unselectedColor,
//                    textAlign = TextAlign.Center,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        }
//
//        // Сама линия с точками
//        Canvas(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(24.dp)
//        ) {
//            val centerY = size.height / 2
//            val centerX = size.width / 2
//            val strokeWidth = 2.dp.toPx()
//
//            // Линия от левого края к центру (не рисуем для первого элемента)
//            if (!isFirst) {
//                drawLine(
//                    color = lineColor,
//                    start = Offset(0f, centerY),
//                    end = Offset(centerX, centerY),
//                    strokeWidth = strokeWidth
//                )
//            }
//
//            // Линия от центра к правому краю (не рисуем для последнего элемента)
//            if (!isLast) {
//                drawLine(
//                    color = lineColor,
//                    start = Offset(centerX, centerY),
//                    end = Offset(size.width, centerY),
//                    strokeWidth = strokeWidth
//                )
//            }
//
//            // Точка
//            drawCircle(
//                color = if (isSelected) selectedColor else lineColor,
//                radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
//                center = Offset(centerX, centerY)
//            )
//        }
//
//        // Контейнер для нижнего текста
//        Box(
//            modifier = Modifier
//                .height(40.dp)
//                .padding(horizontal = 4.dp),
//            contentAlignment = Alignment.TopCenter
//        ) {
//            if (!isTop) {
//                Text(
//                    text = text,
//                    style = MaterialTheme.typography.labelMedium,
//                    color = if (isSelected) selectedColor else unselectedColor,
//                    textAlign = TextAlign.Center,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        }
//    }
//}

@Composable
fun <T> SwipeableTimelineSelector(
    items: List<T>,
    selectedItem: T?,
    nameSelector: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { containerWidth = it.width } // Узнаем ширину контейнера
            .pointerInput(items.size) {
                // Обработка свайпа/перетаскивания
                detectDragGestures { change, _ ->
                    if (containerWidth == 0 || items.isEmpty()) return@detectDragGestures
                    val itemWidth = containerWidth.toFloat() / items.size
                    // Вычисляем индекс элемента под пальцем
                    val index = (change.position.x / itemWidth).toInt().coerceIn(0, items.lastIndex)
                    onItemSelected(items[index])
                }
            }
            .pointerInput(items.size) {
                // Обработка обычного тапа
                detectTapGestures { offset ->
                    if (containerWidth == 0 || items.isEmpty()) return@detectTapGestures
                    val itemWidth = containerWidth.toFloat() / items.size
                    val index = (offset.x / itemWidth).toInt().coerceIn(0, items.lastIndex)
                    onItemSelected(items[index])
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Единая линия на фоне
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Отступы от краев экрана
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Контейнер с элементами
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEachIndexed { index, item ->
                TimelineNode(
                    text = nameSelector(item),
                    isSelected = item == selectedItem,
                    isTop = index % 2 == 0,
                    modifier = Modifier.weight(1f) // Равномерно делим ширину экрана
                )
            }
        }
    }
}

@Composable
private fun TimelineNode(
    text: String,
    isSelected: Boolean,
    isTop: Boolean,
    modifier: Modifier = Modifier
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val circleColor = if (isSelected) selectedColor else MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Верхний текст
        Box(
            modifier = Modifier
                .height(20.dp)
                .padding(horizontal = 2.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (isTop) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) selectedColor else unselectedColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Только точка (без линий)
        Box(
            modifier = Modifier.height(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                drawCircle(
                    color = circleColor,
                    radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx()
                )
            }
        }

        // Нижний текст
        Box(
            modifier = Modifier
                .height(20.dp)
                .padding(horizontal = 2.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (!isTop) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) selectedColor else unselectedColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}