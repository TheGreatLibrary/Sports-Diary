package com.sinya.projects.sportsdiary.ui.themeWidget

import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle

object WidgetType {
    val displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    ) // заголовок в календаре
    val bodyLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
    ) // кнопки
    val bodyMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
    ) // заметки
}
