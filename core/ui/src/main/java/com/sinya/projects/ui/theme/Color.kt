package com.sinya.projects.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.sinya.projects.ui.theme.AppColors.Black
import com.sinya.projects.ui.theme.AppColors.Black600
import com.sinya.projects.ui.theme.AppColors.Black700
import com.sinya.projects.ui.theme.AppColors.Black800
import com.sinya.projects.ui.theme.AppColors.Black865
import com.sinya.projects.ui.theme.AppColors.Black900
import com.sinya.projects.ui.theme.AppColors.Black965
import com.sinya.projects.ui.theme.AppColors.Blue200
import com.sinya.projects.ui.theme.AppColors.Blue300
import com.sinya.projects.ui.theme.AppColors.Blue400
import com.sinya.projects.ui.theme.AppColors.Blue500
import com.sinya.projects.ui.theme.AppColors.Blue600
import com.sinya.projects.ui.theme.AppColors.Blue700
import com.sinya.projects.ui.theme.AppColors.Blue800
import com.sinya.projects.ui.theme.AppColors.Blue900
import com.sinya.projects.ui.theme.AppColors.Gray100
import com.sinya.projects.ui.theme.AppColors.Gray200
import com.sinya.projects.ui.theme.AppColors.Gray400
import com.sinya.projects.ui.theme.AppColors.Gray600
import com.sinya.projects.ui.theme.AppColors.Gray665
import com.sinya.projects.ui.theme.AppColors.Gray700
import com.sinya.projects.ui.theme.AppColors.Gray800
import com.sinya.projects.ui.theme.AppColors.Green700
import com.sinya.projects.ui.theme.AppColors.Orange100
import com.sinya.projects.ui.theme.AppColors.Orange200
import com.sinya.projects.ui.theme.AppColors.Orange210
import com.sinya.projects.ui.theme.AppColors.Orange300
import com.sinya.projects.ui.theme.AppColors.Orange400
import com.sinya.projects.ui.theme.AppColors.Orange500
import com.sinya.projects.ui.theme.AppColors.Orange600
import com.sinya.projects.ui.theme.AppColors.Orange700
import com.sinya.projects.ui.theme.AppColors.Orange800
import com.sinya.projects.ui.theme.AppColors.Orange900
import com.sinya.projects.ui.theme.AppColors.White
import com.sinya.projects.ui.theme.AppColors.White700
import com.sinya.projects.ui.theme.AppColors.White800
import com.sinya.projects.ui.theme.AppColors.White865
import com.sinya.projects.ui.theme.AppColors.White900
import com.sinya.projects.ui.theme.AppColors.White965

object AppColors {
    val Black = Color(0xFF000000)
    val Gray800 = Color(0xFF1C1C1C)
    val Gray700 = Color(0xFF242424)
    val Gray665 = Color(0xA66D6D6D)

    val Gray600 = Color(0xFF3E3E3E)
    val Gray400 = Color(0xFF6E6E6E)
    val Gray200 = Color(0xFF959595)
    val Gray100 = Color(0xFFE8E8E8)
    val White = Color(0xFFFFFFFF)
    val Green700 = Color(0xFF39754C)



    val Orange900 = Color(0xFF2C1100)
    val Orange800 = Color(0xFF3F0100)
    val Orange700 = Color(0xFF6B1E00)
    val Orange600 = Color(0xFF8C3200)
    val Orange500 = Color(0xFFFF710A)
    val Orange400 = Color(0xFFFF724D)
    val Orange300 = Color(0xFFFF7A46)
    val Orange200 = Color(0xFFFF9D60)
    val Orange210 = Color(0x4AFF9D60)
    val Blue900 = Color(0xFF000C25)
    val Blue800 = Color(0xFF003554)
    val Blue700 = Color(0xFF00184B)
    val Blue600 = Color(0xFF00416B)

    val Blue500 = Color(0xFF0953EA)
    val Blue400 = Color(0xFF2FB1FF)
    val Blue300 = Color(0xFF2C6EF6)
    val Blue200 = Color(0xFF4FBAFA)
    val Black900 = Color(0xFF121212)
    val Black965 = Color(0xA6121212)
    val Black600 = Color(0xFF131313)
    val Black700 = Color(0xFF181818)
    val Black800 = Color(0xFF1E1E1E)
    val Black865 = Color(0xA61E1E1E)
    val White900 = Color(0xFFE0E0E0)
    val White965 = Color(0xA6E0E0E0)
    val White800 = Color(0xFFF5F5F5)
    val White865 = Color(0x70A9A9A9)
    val White700 = Color(0xFFDEDEDE)
    val Orange100 = Color(0xFFBE4900)
}

val DarkAppGradients = AppGradients(
    trainingCardGradient = Brush.horizontalGradient(listOf(Orange700, Orange900)),
    trainingCardItemGradient = Brush.horizontalGradient(listOf(Orange600, Orange800)),
    measurementCardGradient = Brush.horizontalGradient(listOf(Blue800, Blue900)),
    measurementCardItemGradient = Brush.horizontalGradient(listOf(Blue600, Blue700)),
    basedCardGradient = Brush.horizontalGradient(listOf(Black800, Black900)),
    basedCard2Gradient = Brush.horizontalGradient(listOf(Black800, Black900), 0f, 800f),
    basedCardGradient65 = Brush.horizontalGradient(listOf(Black865, Black965)),
    basedGradient = Brush.linearGradient(listOf(Black600, Black700), end = Offset(100f, 800f)),
)

val LightAppGradients = AppGradients(
    trainingCardGradient = Brush.horizontalGradient(listOf(Orange400, Orange500)),
    trainingCardItemGradient = Brush.horizontalGradient(listOf(Orange200, Orange300)),
    measurementCardGradient = Brush.horizontalGradient(listOf(Blue400, Blue500)),
    measurementCardItemGradient = Brush.horizontalGradient(listOf(Blue200, Blue300)),
    basedCardGradient = Brush.horizontalGradient(listOf(White800, White900)),
    basedCard2Gradient = Brush.horizontalGradient(listOf(White800, White900), 0f, 800f),
    basedCardGradient65 = Brush.horizontalGradient(listOf(White865, White965)),
    basedGradient = Brush.linearGradient(listOf(White, White700), end = Offset(100f, 800f)),
)

data class AppGradients(
    val trainingCardGradient: Brush,
    val trainingCardItemGradient: Brush,
    val measurementCardGradient: Brush,
    val measurementCardItemGradient: Brush,
    val basedCardGradient: Brush,
    val basedCard2Gradient: Brush,
    val basedCardGradient65: Brush,
    val basedGradient: Brush,
)


val DarkColorScheme = darkColorScheme(
    primary = Green700, // зеленый
    secondary = Orange100, // оранжевый
    tertiary = Blue500, // синий

    background = Black700, // фон

    primaryContainer = Gray800, // основные темные контейнеры
    inverseSurface = White800,
    inverseOnSurface = Gray800,
    secondaryContainer = Gray600, // серые контейнеры
    surfaceContainerHigh = Black900,
    tertiaryContainer = Orange800, // оранжевые карточки

    surface = Gray700, // фон диалогового окна
    surfaceContainer = Gray800, // диалоговое окно
    onSurface = White, // текст
    surfaceContainerLow = White865,
    surfaceContainerLowest = Black865,

    inversePrimary = Gray600,

    onPrimary = White, // текст
    onSecondary = Gray200, // вспомогательный текст
    onPrimaryContainer = White // цвет текста для цветных контейнеров
)

val LightColorScheme = lightColorScheme(
    primary = Green700, // зеленый
    secondary = Orange400, // оранжевый
    tertiary = Blue500, // синий

    background = White700, // фон

    primaryContainer = White, // основные темные контейнеры
    inverseSurface = Gray700,
    inverseOnSurface = White,
    secondaryContainer = Gray200, // серые контейнеры
    surfaceContainerHigh = White900,
    tertiaryContainer = Orange800, // оранжевые карточки

    surface = Gray100, // фон диалога
    surfaceContainer = White, // текстовое поле диалогового окна
    onSurface = Gray400, // текст в текстовом поле диалогового окна
    surfaceContainerLow = Gray665,
    surfaceContainerLowest = White865,

    inversePrimary = White,

    onPrimary = Black, // текст
    onSecondary = Gray400, // вспомогательный текст,
    onPrimaryContainer = White,

)
