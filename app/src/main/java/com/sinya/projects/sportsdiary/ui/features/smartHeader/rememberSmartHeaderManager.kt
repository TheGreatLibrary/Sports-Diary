package com.sinya.projects.sportsdiary.ui.features.smartHeader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
fun rememberSmartHeaderManager(): SmartHeaderManager {
    val manager = remember { SmartHeaderManager() }

    DisposableEffect(Unit) {
        onDispose {
            manager.reset()
        }
    }

    return manager
}