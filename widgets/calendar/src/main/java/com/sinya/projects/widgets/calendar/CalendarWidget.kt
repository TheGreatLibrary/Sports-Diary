package com.sinya.projects.widgets.calendar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
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
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import com.sinya.projects.domain.model.CalendarWidgetUiState
import com.sinya.projects.domain.model.DayOfMonth
import com.sinya.projects.widgets.calendar.themeWidget.WidgetColors
import com.sinya.projects.widgets.calendar.themeWidget.WidgetType
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

val WIDGET_TRIGGER_KEY = longPreferencesKey("widget_refresh_trigger")

class CalendarWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val initialData = loadWidgetState(context)

        provideContent {
            val prefs = currentState<Preferences>()
            val trigger = prefs[WIDGET_TRIGGER_KEY] ?: 0L

            val state by produceState(initialValue = initialData, key1 = trigger) {
                value = loadWidgetState(context)
            }

            GlanceTheme(colors = WidgetColors.colors) {
                ExerciseWidgetContent(state)
            }
        }
    }

    private suspend fun loadWidgetState(context: Context): CalendarWidgetUiState {
        return withContext(Dispatchers.IO) {
            val appContext = context.applicationContext
            val getWidgetDataUseCase = EntryPointAccessors.fromApplication(
                appContext,
                WidgetEntryPoint::class.java
            ).getWidgetDataUseCase()

            getWidgetDataUseCase()
        }
    }

    @Composable
    private fun ExerciseWidgetContent(state: CalendarWidgetUiState) {
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
            localizedContext.getString(com.sinya.projects.ui.R.string.monday_short),
            localizedContext.getString(com.sinya.projects.ui.R.string.tuesday_short),
            localizedContext.getString(com.sinya.projects.ui.R.string.wednesday_short),
            localizedContext.getString(com.sinya.projects.ui.R.string.thursday_short),
            localizedContext.getString(com.sinya.projects.ui.R.string.friday_short),
            localizedContext.getString(com.sinya.projects.ui.R.string.saturday_short),
            localizedContext.getString(com.sinya.projects.ui.R.string.sunday_short)
        )
        val morningState by remember(state.monthDays, state.date) {
            derivedStateOf {
                state.monthDays.fastFirstOrNull { it.date == state.date }?.morningState ?: false
            }
        }
        val buttonText = remember(morningState) {
            if (morningState)
                localizedContext.getString(com.sinya.projects.ui.R.string.finished_morning_exercises)
            else
                localizedContext.getString(com.sinya.projects.ui.R.string.finished_morning_exercises)
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
                        MarkDayMorningExercisesCallback.KEY_CURRENT_STATE to morningState
                    )
                ),
                modifier = GlanceModifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (!morningState) WidgetColors.colors.primary else WidgetColors.colors.secondaryContainer,
                    contentColor = if (!morningState) WidgetColors.colors.onPrimary else WidgetColors.colors.onSecondary,

                    ),
                style = WidgetType.bodyMedium
            )
        }
    }

    @Composable
    private fun CalendarHeader(locale: Locale, today: LocalDate) {
        val fullFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy", locale) }
        val headerText = "${
            today.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { it.titlecase(locale) }
        }, ${today.format(fullFormatter)}"

        Text(
            text = headerText,
            style = WidgetType.displayMedium.copy(color = WidgetColors.colors.onPrimary),
            modifier = GlanceModifier.padding(start = 5.dp)
        )
    }

    @Composable
    private fun WeekdayRow(daysTitle: List<String>, today: LocalDate) {
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

    @SuppressLint("RestrictedApi")
    @Composable
    private fun CalendarRow(days: List<DayOfMonth>, today: LocalDate) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            days.forEach { day ->
                val isToday = (day.date == today)

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
                            text = day.date.dayOfMonth.toString(),
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

    suspend fun forceUpdateAllWidgets(context: Context) {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(CalendarWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[WIDGET_TRIGGER_KEY] = System.currentTimeMillis()
            }
            CalendarWidget().update(context, glanceId)
        }
    }
}