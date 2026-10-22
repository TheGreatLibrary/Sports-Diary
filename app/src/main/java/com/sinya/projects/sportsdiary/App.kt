package com.sinya.projects.sportsdiary

import android.app.Application
import com.sinya.projects.datastore.SettingsEngine
import com.sinya.projects.widgets.calendar.CalendarWidgetInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var engine: SettingsEngine

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch { engine.hydrateCritical() }
        CalendarWidgetInitializer.setupWidgetUpdates(this)
    }
}

