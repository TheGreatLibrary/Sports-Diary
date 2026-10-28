package com.sinya.projects.sportsdiary

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.sinya.projects.datastore.SettingsEngine
import com.sinya.projects.widgets.calendar.CalendarWidgetInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltAndroidApp
class App : Application(), ImageLoaderFactory {

    @Inject
    lateinit var engine: SettingsEngine

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch { engine.hydrateCritical() }
        CalendarWidgetInitializer.setupWidgetUpdates(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                // На Android 9+ используется быстрый системный декодер, на старых — встроенный в Coil
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .build()
    }
}

