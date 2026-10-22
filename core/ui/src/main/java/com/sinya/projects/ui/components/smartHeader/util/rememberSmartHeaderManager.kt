package com.sinya.projects.ui.components.smartHeader.util

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