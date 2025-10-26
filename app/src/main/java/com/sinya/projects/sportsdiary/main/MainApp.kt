package com.sinya.projects.sportsdiary.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sinya.projects.sportsdiary.data.datastore.AppViewModel
import com.sinya.projects.sportsdiary.presentation.home.HomeScreen
import com.sinya.projects.sportsdiary.presentation.menu.MenuScreen
import com.sinya.projects.sportsdiary.presentation.morningExercises.MorningExercisesScreen
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionPageScreen
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionPageViewModel
import com.sinya.projects.sportsdiary.presentation.proportions.ProportionsScreen
import com.sinya.projects.sportsdiary.presentation.settings.SettingsScreen
import com.sinya.projects.sportsdiary.presentation.sportExercisePage.SportExercisePageScreen
import com.sinya.projects.sportsdiary.presentation.sportExercisePage.SportExercisePageViewModel
import com.sinya.projects.sportsdiary.presentation.sportExercises.SportExercisesScreen
import com.sinya.projects.sportsdiary.presentation.statistic.StatisticScreen
import com.sinya.projects.sportsdiary.presentation.trainings.TrainingsScreen
import com.sinya.projects.sportsdiary.presentation.trainings.TrainingViewModel
import com.sinya.projects.sportsdiary.presentation.trainingPage.TrainingPageScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.TrainingPageViewModel
import com.sinya.projects.sportsdiary.ui.theme.SportsDiaryTheme
import com.sinya.projects.sportsdiary.utils.getCurrentRoute

val LocalLocale = staticCompositionLocalOf { Locale.current }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainApp(
    updateLocale: (String) -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val selectedPlanId by viewModel.planId.collectAsState()

    updateLocale(language)
    val currentLocale = remember(language) { Locale(language) }

    val navController = rememberNavController()
    val currentRoute = getCurrentRoute(navController)

    val onBack = { navController.popBackStack() }
    val navigate: (ScreenRoute) -> Unit = { route -> navController.navigate(route) }

    val withBottomBar = listOf(
        ScreenRoute.Home.route,
        ScreenRoute.Menu.route,
        ScreenRoute.Statistic.route,
        ScreenRoute.Settings.route
    )

    CompositionLocalProvider(
        LocalLocale provides currentLocale
    ) {
        SportsDiaryTheme(themeMode, dynamicColor = false) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (withBottomBar.contains(currentRoute)) {
                        NavigationBottomBar(
                            currentRoute = currentRoute,
                            onHomeClick = { navController.navigate(ScreenRoute.Home) },
                            onMenuClick = { navController.navigate(ScreenRoute.Menu) },
                            onStatClick = { navController.navigate(ScreenRoute.Statistic) },
                            onSetClick = { navController.navigate(ScreenRoute.Settings) },
                            onPlusClick = { navController.navigate(ScreenRoute.TrainingPage()) },
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) {
                NavHost(
                    navController = navController,
                    startDestination = ScreenRoute.Home,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<ScreenRoute.Home> {
                        HomeScreen(
                            onTrainingClick = { navigate(ScreenRoute.Training) },
                            onTrainingPlusClick = { navigate(ScreenRoute.TrainingPage()) },
                            onTrainingCardClick = { id -> navigate(ScreenRoute.TrainingPage(id)) },
                            onMorningExercisesClick = { navigate(ScreenRoute.MorningExercises) },
                            onMorningExercisesPlusClick = { navigate(ScreenRoute.MorningExercises) },
                            onProportionsClick = { navigate(ScreenRoute.Proportions) },
                            onProportionsPlusClick = { navigate(ScreenRoute.ProportionPage()) }
                        )
                    }
                    composable<ScreenRoute.Menu> {
                        MenuScreen(
                            onBackClick = { onBack() },
                            onTrainingClick = { navigate(ScreenRoute.Training) },
                            onMorningExercisesClick = { navigate(ScreenRoute.MorningExercises) },
                            onProportionsClick = { navigate(ScreenRoute.Proportions) },
                            onCalculateClick = { navigate(ScreenRoute.Calculate) },
                            onSportsExercisesClick = { navigate(ScreenRoute.SportExercises) }
                        )
                    }
                    composable<ScreenRoute.Statistic> {
                        StatisticScreen(
                            onBackClick = { onBack() },
                        )
                    }
                    composable<ScreenRoute.Settings> {
                        SettingsScreen(
                            onBackClick = { onBack() },
                            toggleTheme = { viewModel.toggleTheme() },
                            setLanguage = { lang ->
                                viewModel.setLanguage(lang)
                                updateLocale(lang)
                              },
                            language = language,
                            themeMode = themeMode
                        )
                    }

                    composable<ScreenRoute.Training> {
                        TrainingsScreen(
                            onBackClick = { onBack() },
                            onTrainingClick = { id -> navigate(ScreenRoute.TrainingPage(id)) }
                        )
                    }
                    composable<ScreenRoute.TrainingPage> { entry ->
                        val args = entry.toRoute<ScreenRoute.TrainingPage>()
                        val vm: TrainingPageViewModel = hiltViewModel()

                        LaunchedEffect(args.id) { vm.init(args.id) }

                        TrainingPageScreen(
                            state = vm.state.value,
                            onEvent = { event -> vm.onEvent(event) },
                            onBackClick = { onBack() },
                            onInfoClick = { },
                        )
                    }

                    composable<ScreenRoute.Proportions> {
                        ProportionsScreen(
                            onBackClick = { onBack() },
                            onProportionClick = { id -> navigate(ScreenRoute.ProportionPage(id)) }
                        )
                    }
                    composable<ScreenRoute.ProportionPage> { entry ->
                        val args = entry.toRoute<ScreenRoute.ProportionPage>()
                        val vm: ProportionPageViewModel = hiltViewModel()

                        LaunchedEffect(args.id) { vm.init(args.id) }

                        ProportionPageScreen(
                            state = vm.state.value,
                            onEvent = { event -> vm.onEvent(event) },
                            onBackClick = { onBack() },
                            onInfoClick = { },
                        )

                    }

                    composable<ScreenRoute.MorningExercises> {
                        MorningExercisesScreen(
                            onBackClick = { onBack() }
                        )
                    }

                    composable<ScreenRoute.Calculate> {

                    }

                    composable<ScreenRoute.SportExercises> {
                        SportExercisesScreen(
                            onBackClick = { onBack() },
                            onExerciseClick = { id -> navigate(ScreenRoute.ExercisePage(id)) }
                        )
                    }
                    composable<ScreenRoute.ExercisePage> { entry ->
                        val args = entry.toRoute<ScreenRoute.ExercisePage>()
                        val vm: SportExercisePageViewModel = hiltViewModel()

                        LaunchedEffect(args.id) { vm.init(args.id) }

                        SportExercisePageScreen(
                            state = vm.state.value,
                            onEvent = { event -> vm.onEvent(event) },
                            onBackClick = { onBack() },
                            onInfoClick = { },
                        )
                    }

                    composable<ScreenRoute.StatisticProportions> {

                    }
                    composable<ScreenRoute.StatisticExercises> {

                    }
                }
            }
        }
    }
}



