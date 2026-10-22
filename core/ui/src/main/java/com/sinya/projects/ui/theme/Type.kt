package com.sinya.projects.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontFamily.Companion.Monospace
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sinya.projects.ui.R

val Golos = FontFamily(
    Font(R.font.golos_medium, FontWeight.Medium),
    Font(R.font.golos_regular, FontWeight.Normal)
)
//val Manrope = FontFamily(
//    Font(R.font.manrope_bold, FontWeight.Bold),
//    Font(R.font.manrope_semibold, FontWeight.SemiBold)
//)
val Unbounded = FontFamily(
    Font(R.font.unbounded_regular, FontWeight.Normal),
    Font(R.font.unbounded_semibold, FontWeight.SemiBold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
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
        fontSize = 13.sp,
    ), // заголовок в карточках

    bodyLarge = TextStyle(
        fontFamily = Golos,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ), // кнопки
    bodyMedium = TextStyle(
        fontFamily = Golos,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ) // заметки
)

