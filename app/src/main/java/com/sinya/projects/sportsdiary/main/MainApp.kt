package com.sinya.projects.sportsdiary.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sinya.projects.sportsdiary.data.datastore.AppViewModel
import com.sinya.projects.sportsdiary.presentation.exercisePage.ExercisePageScreen
import com.sinya.projects.sportsdiary.presentation.exercisePage.ExercisePageViewModel
import com.sinya.projects.sportsdiary.presentation.exercises.ExercisesScreen
import com.sinya.projects.sportsdiary.presentation.home.HomeScreen
import com.sinya.projects.sportsdiary.presentation.menu.MenuScreen
import com.sinya.projects.sportsdiary.presentation.morningExercises.MorningExercisesScreen
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionPageScreen
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionPageViewModel
import com.sinya.projects.sportsdiary.presentation.proportions.ProportionsScreen
import com.sinya.projects.sportsdiary.presentation.settings.SettingsScreen
import com.sinya.projects.sportsdiary.presentation.statistic.StatisticScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.TrainingPageScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.TrainingPageViewModel
import com.sinya.projects.sportsdiary.presentation.trainings.TrainingsScreen
import com.sinya.projects.sportsdiary.ui.features.getCurrentRoute
import com.sinya.projects.sportsdiary.ui.theme.SportsDiaryTheme

@Composable
fun MainApp(
    updateLocale: (String) -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val currentPlanId by viewModel.planId.collectAsState()

    updateLocale(language)
    val currentLocale = remember(language) { Locale(language) }

    val navController = rememberNavController()
    val currentRoute = getCurrentRoute(navController)

    val onBack: () -> Unit = remember(navController) {
        {
            when {
                navController.previousBackStackEntry != null -> {
                    navController.popBackStack()
                }

                navController.currentDestination?.route != ScreenRoute.Home::class.simpleName -> {
                    navController.navigate(ScreenRoute.Home) {

                        launchSingleTop = true
                        popUpTo(ScreenRoute.Home) { inclusive = false }
                    }
                }
            }
        }
    }
    val navigate = remember(navController) {
        { route: ScreenRoute ->
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    CompositionLocalProvider(LocalLocale provides currentLocale) {
        SportsDiaryTheme(themeMode, dynamicColor = false) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (withBottomBar.contains(currentRoute)) {
                        NavigationBottomBar(
                            currentRoute = currentRoute,
                            navigateTo = navigate,
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValue ->
                NavGraph(
                    navController,
                    currentPlanId,
                    language,
                    themeMode,
                    onBack,
                    navigate,
                    updateLocale,
                    Modifier.padding(paddingValue)
                )
            }
        }
    }
}

@Composable
private fun NavGraph(
    navController: NavHostController,
    currentPlanId: Int,
    language: String,
    themeMode: Boolean,
    onBack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    updateLocale: (String) -> Unit,
    modifier: Modifier,
    viewModel: AppViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Home,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<ScreenRoute.Home> {
            HomeScreen(
                currentPlanId = currentPlanId,
                navigateTo = navigateTo
            )
        }
        composable<ScreenRoute.Menu> {
            MenuScreen(
                onBackClick = onBack,
                navigateTo = navigateTo
            )
        }
        composable<ScreenRoute.Statistic> {
            StatisticScreen(
                onBackClick = onBack,
            )
        }
        composable<ScreenRoute.Settings> {
            SettingsScreen(
                onBackClick = onBack,
                toggleTheme = viewModel::toggleTheme,
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
                onBackClick = onBack,
                onTrainingClick = { id -> navigateTo(ScreenRoute.TrainingPage(id)) }
            )
        }
        composable<ScreenRoute.TrainingPage> { entry ->
            val args = entry.toRoute<ScreenRoute.TrainingPage>()

            TrainingPageScreen(
                id = args.id,
                onBackClick = onBack
            )
        }

        composable<ScreenRoute.Proportions> {
            ProportionsScreen(
                onBackClick = onBack,
                onProportionClick = { id -> navigateTo(ScreenRoute.ProportionPage(id)) },
            )
        }
        composable<ScreenRoute.ProportionPage> { entry ->
            val args = entry.toRoute<ScreenRoute.ProportionPage>()

            ProportionPageScreen(
                id = args.id,
                onBackClick = onBack,
            )
        }

        composable<ScreenRoute.MorningExercises> {
            MorningExercisesScreen(
                onBackClick = onBack,
                currentPlanId = currentPlanId,
                onPlanClick = viewModel::setPlanMorningId
            )
        }

        composable<ScreenRoute.Calculate> {

        }

        composable<ScreenRoute.SportExercises> {
            ExercisesScreen(
                onBackClick = onBack,
                onExerciseClick = { id -> navigateTo(ScreenRoute.ExercisePage(id)) }
            )
        }
        composable<ScreenRoute.ExercisePage> { entry ->
            val args = entry.toRoute<ScreenRoute.ExercisePage>()
            val vm: ExercisePageViewModel = hiltViewModel()

            LaunchedEffect(args.id) { vm.init(args.id) }

            ExercisePageScreen(
                state = vm.state.value,
                onEvent = { event -> vm.onEvent(event) },
                onBackClick = onBack,
                onInfoClick = { },
            )
        }

        composable<ScreenRoute.StatisticProportions> {

        }
        composable<ScreenRoute.StatisticExercises> {

        }
    }
}

private val LocalLocale = staticCompositionLocalOf { Locale.current }

private val withBottomBar = listOf(
    ScreenRoute.Home.route,
    ScreenRoute.Menu.route,
    ScreenRoute.Statistic.route,
    ScreenRoute.Settings.route
)



