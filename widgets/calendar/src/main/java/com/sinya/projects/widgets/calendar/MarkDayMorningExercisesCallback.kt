package com.sinya.projects.widgets.calendar

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import com.sinya.projects.domain.model.MorningEntry
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

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
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            WidgetEntryPoint::class.java
        )

        withContext(Dispatchers.IO) {
            val markExerciseUseCase = entryPoint.markDayMorningExerciseUseCase()
            val currentPlanId = entryPoint.morningRoutineIdProvider().morningRoutineId.first()

            markExerciseUseCase(
                morningState = currentMorningState,
                MorningEntry(
                    id = 0,
                    note = null,
                    date = LocalDate.now().toString(),
                    categoryId = currentPlanId
                )
            )
        }

        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[WIDGET_TRIGGER_KEY] = System.currentTimeMillis()
        }

        CalendarWidget().update(context, glanceId)
    }
}