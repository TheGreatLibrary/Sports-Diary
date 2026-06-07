package com.sinya.projects.sportsdiary

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.core.utils.updateLocale
import com.sinya.projects.sportsdiary.navigation.MainApp
import com.sinya.projects.sportsdiary.ui.theme.LocalSettingsEngine
import com.sinya.projects.sportsdiary.ui.theme.SportsDiaryTheme
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.drop

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var engine: SettingsEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        updateLocale(engine.uiState.value.language)

        applySystem()

        setContent {
            App(engine)
        }
    }

    private fun applySystem() {
        enableEdgeToEdge()
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            isAppearanceLightStatusBars = !engine.uiState.value.dark
        }

        val isTablet = resources.configuration.smallestScreenWidthDp >= 600
        requestedOrientation = if (isTablet) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        }
    }

    @Composable
    private fun App(engine: SettingsEngine) {
        val config by engine.uiState.collectAsStateWithLifecycle()

        DisposableEffect(config.dark) {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !config.dark
            }
            onDispose {}
        }

        LaunchedEffect(Unit) {
            snapshotFlow { config.language }
                .drop(1)
                .collect { lang ->
                    updateLocale(lang)
                }
        }

        CompositionLocalProvider(
            LocalSettingsEngine provides engine
        ) {
            SportsDiaryTheme(darkTheme = config.dark, dynamicColor = false) {
                MainApp()
            }
        }
    }
}

