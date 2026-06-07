package com.sinya.projects.sportsdiary

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sinya.projects.sportsdiary.widgets.calendarWidget.WidgetUpdateWorker
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var engine: SettingsEngine

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch { engine.hydrateCritical() }
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

