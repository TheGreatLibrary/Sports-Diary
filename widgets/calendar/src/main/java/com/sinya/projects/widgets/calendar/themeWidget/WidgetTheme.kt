package com.sinya.projects.widgets.calendar.themeWidget

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.glance.material3.ColorProviders
import com.sinya.projects.ui.theme.AppColors.Black
import com.sinya.projects.ui.theme.AppColors.Blue500
import com.sinya.projects.ui.theme.AppColors.Blue700
import com.sinya.projects.ui.theme.AppColors.Gray100
import com.sinya.projects.ui.theme.AppColors.Gray200
import com.sinya.projects.ui.theme.AppColors.Gray600
import com.sinya.projects.ui.theme.AppColors.Gray700
import com.sinya.projects.ui.theme.AppColors.Gray800
import com.sinya.projects.ui.theme.AppColors.Green700
import com.sinya.projects.ui.theme.AppColors.Orange500
import com.sinya.projects.ui.theme.AppColors.Orange600
import com.sinya.projects.ui.theme.AppColors.Orange700
import com.sinya.projects.ui.theme.AppColors.Orange800
import com.sinya.projects.ui.theme.AppColors.White

object WidgetColors {
    val colors = ColorProviders(
        light = lightColorScheme(
            primary = Green700,
            secondary = Orange500,
            tertiary = Blue500,
            background = Gray100,
            primaryContainer = White,
            secondaryContainer = Gray200,
            tertiaryContainer = Orange600,
            onPrimary = Black,
            onSecondary = Gray600
        ),
        dark = darkColorScheme(
            primary = Green700,
            secondary = Orange500,
            tertiary = Blue500,
            background = Gray700,
            primaryContainer = Gray800,
            secondaryContainer = Gray600,
            tertiaryContainer = Orange600,
            onPrimary = White,
            onSecondary = Gray200
        )
    )
}