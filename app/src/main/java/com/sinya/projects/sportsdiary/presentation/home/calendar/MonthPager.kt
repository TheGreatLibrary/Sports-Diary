package com.sinya.projects.sportsdiary.presentation.home.calendar

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sinya.projects.sportsdiary.domain.model.DayOfMonth
import java.time.LocalDate

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun MonthPager(
    days: List<DayOfMonth>,
    pickDay: (LocalDate) -> Unit,
    expanded: Boolean,
    today: LocalDate
) {
    var lastYm by remember { mutableIntStateOf(today.year) }
    val direction = remember(today.year) {
        val dir = if (today.year > lastYm) 1 else -1
        lastYm = today.year
        dir
    }

    AnimatedContent(
        targetState = today.month,
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
            pickDay,
            expanded = expanded,
            today = today
        )
    }
}