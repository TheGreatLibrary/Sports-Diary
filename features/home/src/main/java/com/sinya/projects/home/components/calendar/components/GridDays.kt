package com.sinya.projects.home.components.calendar.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.DayOfMonth
import java.time.LocalDate

@Composable
internal fun GridDays(
    days: List<DayOfMonth>,
    onCheckClick: (DayOfMonth) -> Unit,
    pickDay: (LocalDate) -> Unit,
    selectionState: Boolean,
    onSelectionChange: () -> Unit,
    today: DayOfMonth
) {
    val dayBounds = remember { mutableMapOf<Int, Rect>() }

    // Храним индексы для вычисления диапазона свайпа
    var startDragIndex by remember { mutableIntStateOf(-1) }
    var currentDragIndex by remember { mutableIntStateOf(-1) }
    // Храним предыдущий выделенный диапазон, чтобы понимать, куда движется палец
    var previousRange by remember { mutableStateOf(emptySet<Int>()) }

    // Как выйти из режима: обработчик системной кнопки "Назад" (свайп от края экрана)
    BackHandler(enabled = selectionState) {
        onSelectionChange() // Возвращает selectionState в false
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(days) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        // Включаем чекбоксы
                        if (!selectionState) onSelectionChange()

                        // Ищем карточку, с которой начали (здесь нужно точное попадание)
                        val entry = dayBounds.entries.find { it.value.contains(offset) }
                        if (entry != null) {
                            startDragIndex = entry.key
                            currentDragIndex = entry.key
                            previousRange = setOf(startDragIndex)
                            onCheckClick(days[startDragIndex]) // Выделяем самый первый день
                        }
                    },
                    onDrag = { change, _ ->
                        if (startDragIndex == -1) return@detectDragGesturesAfterLongPress

                        // МАГИЯ №1: Ищем БЛИЖАЙШИЙ центр карточки к пальцу.
                        // Это решает проблему "щелей" в 12.dp и выхода пальца за границы сетки!
                        val closestEntry = dayBounds.entries.minByOrNull {
                            (it.value.center - change.position).getDistanceSquared()
                        }

                        if (closestEntry != null && closestEntry.key != currentDragIndex) {
                            currentDragIndex = closestEntry.key

                            // МАГИЯ №2: Вычисляем математический диапазон (Range)
                            val min = minOf(startDragIndex, currentDragIndex)
                            val max = maxOf(startDragIndex, currentDragIndex)
                            val newRange = (min..max).toSet()

                            // Находим разницу: что нужно выделить, а с чего снять выделение (если повели палец назад)
                            val toAdd = newRange - previousRange
                            val toRemove = previousRange - newRange

                            // Применяем клик ТОЛЬКО к тем карточкам, статус которых реально поменялся
                            toAdd.forEach { i -> onCheckClick(days[i]) }
                            toRemove.forEach { i -> onCheckClick(days[i]) }

                            // Запоминаем текущий диапазон для следующего шага свайпа
                            previousRange = newRange
                        }
                    },
                    onDragEnd = {
                        startDragIndex = -1
                        currentDragIndex = -1
                        previousRange = emptySet()
                    },
                    onDragCancel = {
                        startDragIndex = -1
                        currentDragIndex = -1
                        previousRange = emptySet()
                    }
                )
            }
            .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        maxItemsInEachRow = 7
    ) {
        days.forEachIndexed { index, dayCell ->
            val isToday = dayCell.date == today.date

            DayCard(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    dayBounds[index] = coordinates.boundsInParent()
                },
                title = dayCell.date.dayOfMonth.toString(),
                isToday = isToday,
                isCurrentMonth = dayCell.currentMonth,
                onClick = { pickDay(dayCell.date) },
                selectionState = selectionState,
                selected = dayCell.state,
                onCheckClick = { onCheckClick(dayCell) },
                brushBorder = Brush.linearGradient(
                    colors = listOf(
                        if (dayCell.trainingState) MaterialTheme.colorScheme.secondary
                        else Color.Transparent,
                        if (dayCell.morningState) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                    ),
                    start = Offset(0f, 25f),
                    end = Offset(0f, 100f),
                    tileMode = TileMode.Clamp
                )
            )
        }
    }
}