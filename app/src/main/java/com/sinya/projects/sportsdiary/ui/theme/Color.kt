package com.sinya.projects.sportsdiary.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Black
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Blue700
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Gray100
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Gray200
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Gray400
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Gray600
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Gray700
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Gray800
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Green700
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Orange700
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Orange800
import com.sinya.projects.sportsdiary.ui.theme.AppColors.White

object AppColors {
    val Black = Color(0xFF000000)
    val Gray800 = Color(0xFF1C1C1C)
    val Gray700 = Color(0xFF242424)
    val Gray600 = Color(0xFF3E3E3E)
    val Gray400 = Color(0xFF6E6E6E)
    val Gray200 = Color(0xFF959595)
    val Gray100 = Color(0xFFDCDCDC)
    val White = Color(0xFFFFFFFF)
    val Orange800 = Color(0xFF792E00)
    val Orange700 = Color(0xFFBE4900)
    val Green700 = Color(0xFF39754C)
    val Blue700 = Color(0xFF126584)
}


val DarkColorScheme = darkColorScheme(
    primary = Green700, // зеленый
    secondary = Orange700, // оранжевый
    tertiary = Blue700, // синий

    background = Gray700, // фон

    primaryContainer = Gray800, // основные темные контейнеры
    secondaryContainer = Gray600, // серые контейнеры
    tertiaryContainer = Orange800, // оранжевые карточки

    surface = Gray700, // фон диалогового окна
    surfaceContainer = Gray800, // диалоговое окно
    onSurface = White, // текст

    inversePrimary = Gray600,

    onPrimary = White, // текст
    onSecondary = Gray200, // вспомогательный текст
    onPrimaryContainer = White // цвет текста для цветных контейнеров
)

val LightColorScheme = lightColorScheme(
    primary = Green700, // зеленый
    secondary = Orange700, // оранжевый
    tertiary = Blue700, // синий

    background = Gray100, // фон

    primaryContainer = White, // основные темные контейнеры
    secondaryContainer = Gray200, // серые контейнеры
    tertiaryContainer = Orange800, // оранжевые карточки

    surface = White, // фон диалога
    surfaceContainer = Gray100, // текстовое поле диалогового окна
    onSurface = Gray400, // текст в текстовом поле диалогового окна

    inversePrimary = White,

    onPrimary = Black, // текст
    onSecondary = Gray400, // вспомогательный текст,
    onPrimaryContainer = White
)
