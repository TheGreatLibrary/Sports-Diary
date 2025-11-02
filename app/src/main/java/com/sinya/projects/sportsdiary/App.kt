package com.sinya.projects.sportsdiary

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sinya.projects.sportsdiary.widgets.calendarWidget.WidgetUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setupWidgetUpdates()
    }

    private fun setupWidgetUpdates() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        val updateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
            30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "calendar_widget_periodic_update",
                ExistingPeriodicWorkPolicy.KEEP,
                updateRequest
            )
    }
}

