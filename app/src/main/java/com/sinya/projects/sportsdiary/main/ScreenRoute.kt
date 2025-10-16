package com.sinya.projects.sportsdiary.main

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

    @Serializable data object MorningExercises : ScreenRoute("MorningExercises")
    @Serializable data object Calculate : ScreenRoute("Calculate")

    @Serializable data object SportExercises : ScreenRoute("SportExercises")
    @Serializable data class ExercisePage(val id: Int) : ScreenRoute("ExercisePage")

    @Serializable data object StatisticProportions : ScreenRoute("StatisticProportions")
    @Serializable data object StatisticExercises : ScreenRoute("StatisticExercises")
}