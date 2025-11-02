package com.sinya.projects.sportsdiary.widgets.calendarWidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import com.sinya.projects.sportsdiary.App
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.data.datastore.DataStoreManager
import com.sinya.projects.sportsdiary.presentation.home.DayOfMonth
import com.sinya.projects.sportsdiary.ui.themeWidget.WidgetColors
import com.sinya.projects.sportsdiary.ui.themeWidget.WidgetType
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = loadWidgetState(context)

        provideContent {
            GlanceTheme(colors = WidgetColors.colors) {
                ExerciseWidgetContent(state)
            }
        }
    }

    private suspend fun loadWidgetState(context: Context): CalendarWidgetUiState {
        return withContext(Dispatchers.IO) {
            val appContext = context.applicationContext as App
            val getWidgetDataUseCase = EntryPointAccessors.fromApplication(
                appContext,
                WidgetEntryPoint::class.java
            ).getWidgetDataUseCase()

            getWidgetDataUseCase()
        }
    }

    @Composable
    private fun ExerciseWidgetContent(
        state: CalendarWidgetUiState
    ) {
        val locale = remember { Locale.getDefault() }
        val today = LocalDate.now()
        val context = LocalContext.current
        val localizedContext = remember(locale) {
            context.createConfigurationContext(
                context.resources.configuration.apply {
                    setLocale(locale)
                }
            )
        }
        val daysTitle = listOf(
            localizedContext.getString(R.string.monday_short),
            localizedContext.getString(R.string.tuesday_short),
            localizedContext.getString(R.string.wednesday_short),
            localizedContext.getString(R.string.thursday_short),
            localizedContext.getString(R.string.friday_short),
            localizedContext.getString(R.string.saturday_short),
            localizedContext.getString(R.string.sunday_short)
        )
        val buttonText = remember(state.morningState) {
            if (state.morningState)
                localizedContext.getString(R.string.finished_morning_exercises)
            else
                localizedContext.getString(R.string.finish_morning_exercises)
        }


        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp)
                .background(WidgetColors.colors.primaryContainer)
                .cornerRadius(16.dp),
        ) {
            CalendarHeader(
                locale = locale,
                today = today
            )

            Spacer(GlanceModifier.height(12.dp))

            WeekdayRow(
                daysTitle = daysTitle,
                today = today
            )

            Spacer(GlanceModifier.height(12.dp))

            CalendarRow(state.monthDays, today)

            Spacer(GlanceModifier.height(12.dp))

            Button(
                text = buttonText,
                onClick = actionRunCallback<MarkDayMorningExercisesCallback>(
                    parameters = actionParametersOf(
                        MarkDayMorningExercisesCallback.KEY_CURRENT_STATE to state.morningState
                    )
                ),
                modifier = GlanceModifier.fillMaxWidth(),
//                enabled = !state.morningState,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (!state.morningState) WidgetColors.colors.primary else WidgetColors.colors.secondaryContainer,
                    contentColor = if (!state.morningState) WidgetColors.colors.onPrimary else WidgetColors.colors.onSecondary,

                    ),
                style = WidgetType.bodyMedium
            )
        }
    }

    @Composable
    private fun CalendarHeader(
        locale: Locale,
        today: LocalDate
    ) {
        val fullFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy", locale) }
        val headerText = "${
            today.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, locale)
                .replaceFirstChar { it.titlecase(locale) }
        }, ${today.format(fullFormatter)}"

        Text(
            text = headerText,
            style = WidgetType.displayMedium.copy(color = WidgetColors.colors.onPrimary),
            modifier = GlanceModifier.padding(start = 5.dp)
        )
    }

    @Composable
    private fun WeekdayRow(
        daysTitle: List<String>,
        today: LocalDate
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val dayIndex = today.dayOfWeek.value
            daysTitle.forEachIndexed { i, title ->
                Box(
                    modifier = GlanceModifier.defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = WidgetType.displayMedium.copy(
                            color = if (i + 1 == dayIndex)
                                WidgetColors.colors.onPrimary
                            else
                                WidgetColors.colors.onSecondary
                        )
                    )
                }
            }
        }
    }


    @Composable
    private fun CalendarRow(days: List<DayOfMonth>, today: LocalDate) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            days.forEach { day ->
                val isToday = (
                        day.year == today.year &&
                                day.month == today.monthValue &&
                                day.day == today.dayOfMonth
                        )

                Box(
                    modifier = GlanceModifier.defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = GlanceModifier
                            .size(34.dp)
                            .cornerRadius(100.dp),
                    ) {
                        Box(
                            modifier = GlanceModifier
                                .width(17.dp)
                                .fillMaxHeight()
                                .background(
                                    if (day.morningState)
                                        WidgetColors.colors.primary
                                    else
                                        ColorProvider(Color.Transparent)
                                ),
                        ) {}
                        Box(
                            modifier = GlanceModifier
                                .width(17.dp)
                                .fillMaxHeight()
                                .background(
                                    if (day.trainingState)
                                        WidgetColors.colors.secondary
                                    else
                                        ColorProvider(Color.Transparent)
                                )
                        ) {}
                    }
                    Box(
                        modifier = GlanceModifier
                            .size(29.dp)
                            .background(
                                if (isToday)
                                    WidgetColors.colors.onPrimary
                                else
                                    WidgetColors.colors.secondaryContainer
                            )
                            .cornerRadius(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.day.toString(),
                            style = WidgetType.bodyLarge.copy(
                                color = when {
                                    !day.currentMonth -> WidgetColors.colors.onSecondary
                                    isToday -> WidgetColors.colors.primaryContainer
                                    else -> WidgetColors.colors.onPrimary
                                },
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}


class MarkDayMorningExercisesCallback : ActionCallback {

    companion object {
        val KEY_CURRENT_STATE = ActionParameters.Key<Boolean>("current_morning_state")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val currentMorningState = parameters[KEY_CURRENT_STATE] ?: false
        val appContext = context.applicationContext as App
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            WidgetEntryPoint::class.java
        )
        val markExerciseUseCase = entryPoint.markDayMorningExerciseUseCase()
        val currentPlanId = entryPoint.dataStoreManager().getPlanMorningId().first()

        markExerciseUseCase(
            morningState = currentMorningState,
            DataMorning(
                id = 0,
                note = null,
                date = LocalDate.now().toString(),
                planId = currentPlanId
            )
        )

        delay(500)

        CalendarWidget().updateAll(context)
    }
}

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun dataStoreManager(): DataStoreManager
    fun getWidgetDataUseCase(): GetWidgetDataUseCase
    fun markDayMorningExerciseUseCase(): MarkDayMorningExerciseUseCase
}
