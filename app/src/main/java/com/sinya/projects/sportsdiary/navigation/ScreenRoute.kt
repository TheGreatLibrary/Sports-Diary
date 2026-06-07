package com.sinya.projects.sportsdiary.navigation

import androidx.navigation.NavDestination
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute(val route: String) {
    @Serializable data object Home : ScreenRoute("Home")
    @Serializable data object Menu : ScreenRoute("Menu")
    @Serializable data object Statistic : ScreenRoute("Statistic")
    @Serializable data object Settings : ScreenRoute("Settings")

    @Serializable data object Proportions : ScreenRoute("Proportions")
    @Serializable data class ProportionPage(val id: Int? = null) : ScreenRoute("ProportionPage")

    @Serializable data object Training : ScreenRoute("Training")
    @Serializable data class TrainingPage(val id: Int? = null) : ScreenRoute("TrainingPage")

    @Serializable data object Categories : ScreenRoute("Categories")
    @Serializable data class CategoryPage(val id: Int? = null) : ScreenRoute("CategoryPage")

    @Serializable data object MorningExercises : ScreenRoute("MorningExercises")
    @Serializable data object Calculate : ScreenRoute("Calculate")

    @Serializable data object SportExercises : ScreenRoute("SportExercises")
    @Serializable data class ExercisePage(val id: Int) : ScreenRoute("ExercisePage")
    @Serializable data class ExerciseEdit(val id: Int?) : ScreenRoute("ExerciseEdit")

    @Serializable data object StatisticProportions : ScreenRoute("StatisticProportions")
    @Serializable data object StatisticExercises : ScreenRoute("StatisticExercises")
}

val WITH_BOTTOM_BAR = setOf(
    ScreenRoute.Home.route,
    ScreenRoute.Menu.route,
    ScreenRoute.Statistic.route,
    ScreenRoute.Settings.route
)

val NavDestination.simpleName: String?
    get() = route
        ?.substringAfterLast('.')
        ?.substringBefore('/')
        ?.substringBefore('?')
