package com.sinya.projects.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import java.time.format.DateTimeFormatter


@Composable
fun SportsDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}



val LocalDateFormatDMY = staticCompositionLocalOf<DateTimeFormatter> {
    error("DateTimeFormatter not provider")
}
val LocalDateFormatMY = staticCompositionLocalOf<DateTimeFormatter> {
    error("DateTimeFormatter not provider")
}

val LocalAppGradients = staticCompositionLocalOf<AppGradients> {
    error("No gradients provided! Make sure to wrap your UI in AppTheme.")
}
