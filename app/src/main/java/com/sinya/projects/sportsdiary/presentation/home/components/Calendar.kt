package com.sinya.projects.sportsdiary.presentation.home.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.home.DayOfMonth
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.CustomButton
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Calendar(
    onExtended: () -> Unit,
    onButtonMorningClick: () -> Unit,
    onShift: (Int) -> Unit,
    daysTitle: List<String> = listOf(
        stringResource(R.string.monday_short),
        stringResource(R.string.tuesday_short),
        stringResource(R.string.wednesday_short),
        stringResource(R.string.thursday_short),
        stringResource(R.string.friday_short),
        stringResource(R.string.saturday_short),
        stringResource(R.string.sunday_short)
    ),
    daysNumber: List<DayOfMonth>,
    morningState: Boolean,
    expandedCalendar: Boolean,
    year: Int,
    month: Int,
) {
    val ym = remember(year, month) { YearMonth.of(year, month) }
    val swipeThreshold = with(LocalDensity.current) { 56.dp.toPx() }
    var dragX by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(ym) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, amount -> dragX += amount },
                    onDragEnd = {
                        if (kotlin.math.abs(dragX) > swipeThreshold) {
                            if (dragX < 0) onShift(+1) else onShift(-1)
                        }
                        dragX = 0f
                    },
                    onDragCancel = { dragX = 0f }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarBody(
            onButtonMorningClick,
            daysTitle,
            daysNumber,
            morningState,
            expandedCalendar,
            ym
        )

        ExpandButton(
            expanded = expandedCalendar,
            onClick = onExtended
        )
    }
}


@Composable
private fun CalendarBody(
    onButtonMorningClick: () -> Unit,
    daysTitle: List<String>,
    daysNumber: List<DayOfMonth>,
    morningState: Boolean,
    expandedCalendar: Boolean,
    ym: YearMonth
) {
    val today = remember { LocalDate.now() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
            .padding(top = 50.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CalendarHeader(
            ym = ym,
            today = today
        )

        WeekdayRow(
            daysTitle = daysTitle,
            highlightToday = ym == YearMonth.from(today),
            today = today
        )

        MonthPager(
            ym = ym,
            days = daysNumber,
            expanded = expandedCalendar,
            today = today
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterHorizontally),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Legend()
            Spacer(Modifier.width(30.dp))
            CustomButton(
                onClick = onButtonMorningClick,
                containerColor = if (!morningState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (!morningState) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondary,
                text = if (!morningState)
                    stringResource(R.string.finish_morning_exercises)
                else
                    stringResource(R.string.finished_morning_exercises)
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    ym: YearMonth,
    today: LocalDate
) {
    val ru = remember { Locale.getDefault() }
    val fullFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy", ru) }
    val monthFormatter = remember { DateTimeFormatter.ofPattern("LLLL yyyy", ru) }

    val isCurrentMonth = ym == YearMonth.from(today)
    val headerText = if (isCurrentMonth) {
        val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, ru)
            .replaceFirstChar { it.titlecase(ru) }
        "$dayName, ${today.format(fullFormatter)}"
    } else {
        ym.atDay(1).format(monthFormatter).replaceFirstChar { it.titlecase(ru) }
    }

    Text(
        text = headerText,
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
private fun WeekdayRow(
    daysTitle: List<String>,
    highlightToday: Boolean,
    today: LocalDate
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val dayIndex = today.dayOfWeek.value

        daysTitle.forEachIndexed { i, title ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (highlightToday && i + 1 == dayIndex)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
private fun MonthPager(
    ym: YearMonth,
    days: List<DayOfMonth>,
    expanded: Boolean,
    today: LocalDate
) {
    var lastYm by remember { mutableStateOf(ym) }
    val direction = remember(ym) {
        val dir = if (ym > lastYm) 1 else -1
        lastYm = ym
        dir
    }

    AnimatedContent(
        targetState = ym,
        transitionSpec = {
            val offset = if (direction > 0) {
                { full: Int -> full }
            } else {
                { full: Int -> -full }
            }
            slideInHorizontally(
                animationSpec = tween(220, easing = FastOutSlowInEasing),
                initialOffsetX = offset
            ) + fadeIn(tween(180)) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(200, easing = FastOutSlowInEasing),
                        targetOffsetX = offset
                    ) + fadeOut(tween(160))
        },
        label = "monthSlide"
    ) { _ ->
        MonthGrid(
            days = days,
            expanded = expanded,
            today = today
        )
    }
}

@Composable
private fun MonthGrid(
    days: List<DayOfMonth>,
    expanded: Boolean,
    today: LocalDate
) {
    val weeks = remember(days) { days.chunked(7) }
    val currentWeekIndex = remember(days, today) {
        weeks.indexOfFirst { row ->
            row.any { it.year == today.year && it.month == today.monthValue && it.day == today.dayOfMonth }
        }.let { if (it == -1) 0 else it }
    }
    val rows = if (expanded) weeks else listOf(weeks[currentWeekIndex])

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { dayCell ->
                    val isToday = (
                            dayCell.year == today.year &&
                                    dayCell.month == today.monthValue &&
                                    dayCell.day == today.dayOfMonth
                            )

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .size(33.dp)
                                .aspectRatio(1f)
                                .background(
                                    shape = MaterialTheme.shapes.extraLarge,
                                    color = if (isToday) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.secondaryContainer
                                )
                                .border(
                                    width = 2.dp,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    brush = Brush.linearGradient(
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
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayCell.day.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = when {
                                    !dayCell.currentMonth -> MaterialTheme.colorScheme.onSecondary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Legend() {
    Column {
        LegendDot(
            text = stringResource(R.string.morning_exercises),
            color = MaterialTheme.colorScheme.primary
        )
        LegendDot(
            text = stringResource(R.string.training),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun LegendDot(
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(shape = MaterialTheme.shapes.extraLarge, color = color)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun ExpandButton(
    expanded: Boolean,
    onClick: () -> Unit
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "arrowRotation"
    )

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(35.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            contentDescription = if (expanded) "Collapse" else "Expand",
            painter = painterResource(R.drawable.ic_arrow),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.rotate(arrowRotation)
        )
    }
}