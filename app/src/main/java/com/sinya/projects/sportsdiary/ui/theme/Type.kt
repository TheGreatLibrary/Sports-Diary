package com.sinya.projects.sportsdiary.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sinya.projects.sportsdiary.R

val Nunito = FontFamily(
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_semibold, FontWeight.SemiBold)
)
val Unbounded = FontFamily(
    Font(R.font.unbounded_regular, FontWeight.Normal),
    Font(R.font.unbounded_semibold, FontWeight.SemiBold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
    ), // цифры в статистике
    displayMedium = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ), // календарь
    displaySmall = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ), // упражнения заголовок

    titleLarge = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
    ), // заголовок страниц
    titleMedium = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
    ), // заголовок категорий
    titleSmall = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ), // заголовок в карточках

    bodyLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ), // кнопки
    bodyMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
    ) // заметки
)