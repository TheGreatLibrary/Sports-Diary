package com.sinya.projects.sportsdiary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Green700, // зеленый
    secondary = Orange700, // оранжевый
    tertiary = Blue700, // синий

    background = Gray700, // фон

    primaryContainer = Gray800, // основные темные контейнеры
    secondaryContainer = Gray600, // серые контейнеры
    tertiaryContainer = Orange800, // оранжевые карточки

    onPrimary = White, // текст
    onSecondary = Gray200 // вспомогательный текст
)

private val LightColorScheme = lightColorScheme(
    primary = Green700, // зеленый
    secondary = Orange700, // оранжевый
    tertiary = Blue700, // синий

    background = Gray100, // фон

    primaryContainer = White, // основные темные контейнеры
    secondaryContainer = Gray200, // серые контейнеры
    tertiaryContainer = Orange800, // оранжевые карточки

    onPrimary = Black, // текст
    onSecondary = Gray600 // вспомогательный текст
)

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