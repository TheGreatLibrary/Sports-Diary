package com.sinya.projects.sportsdiary.widgets.calendarWidget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("WidgetUpdateWorker", "Updating calendar widget...")
            CalendarWidget().updateAll(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Log.e("WidgetUpdateWorker", "Failed to update widget", e)
            Result.retry()
        }
    }
}