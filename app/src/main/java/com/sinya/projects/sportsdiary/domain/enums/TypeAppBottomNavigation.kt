package com.sinya.projects.sportsdiary.domain.enums

import androidx.annotation.DrawableRes
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.main.ScreenRoute

enum class TypeAppBottomNavigation(
    val route: ScreenRoute,
    @DrawableRes val icon: Int
) {
    HOME(ScreenRoute.Home, R.drawable.nav_home),
    MENU(ScreenRoute.Menu, R.drawable.nav_menu),
    STAT(ScreenRoute.Statistic, R.drawable.nav_stat),
    SETT(ScreenRoute.Settings, R.drawable.nav_set),
    PLUS(ScreenRoute.TrainingPage(), R.drawable.ic_plus);

    companion object {
        fun getFirstPartList(): List<TypeAppBottomNavigation> = listOf(HOME, MENU)

        fun getSecondPartList(): List<TypeAppBottomNavigation> = listOf(STAT, SETT)

        fun getPlus(): TypeAppBottomNavigation = PLUS
    }
}