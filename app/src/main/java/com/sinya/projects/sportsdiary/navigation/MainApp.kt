package com.sinya.projects.sportsdiary.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sinya.projects.backup.BackupScreen
import com.sinya.projects.calculator.CalculatorScreen
import com.sinya.projects.domain.model.TypeBottomNavigation
import com.sinya.projects.editexercise.ExerciseEditScreen
import com.sinya.projects.exercises.ExercisesScreen
import com.sinya.projects.health.HealthDataScreen
import com.sinya.projects.home.HomeScreen
import com.sinya.projects.menu.MenuScreen
import com.sinya.projects.notes.NotesScreen
import com.sinya.projects.pageExercise.ExercisePageScreen
import com.sinya.projects.pagecategory.RoutinePageScreen
import com.sinya.projects.pageproportion.ProportionPageScreen
import com.sinya.projects.pagetraining.TrainingPageScreen
import com.sinya.projects.proportions.ProportionsScreen
import com.sinya.projects.routines.CategoriesScreen
import com.sinya.projects.settings.SettingsScreen
import com.sinya.projects.statistic.StatisticScreen
import com.sinya.projects.statisticdiagram.StatisticDiagramScreen
import com.sinya.projects.trainings.TrainingsScreen
import com.sinya.projects.ui.R
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.simpleName
    val gradients = LocalAppGradients.current

    val withBottomBar = remember(currentRoute) { currentRoute in WITH_BOTTOM_BAR }

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
        { route: ScreenRoute, lastRoute: ScreenRoute? ->
            navController.navigate(route) {
                launchSingleTop = true
                if (lastRoute != null) popUpTo(lastRoute::class) {
                    inclusive = true
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
//                MaterialTheme.colorScheme.background
                brush = gradients.basedGradient
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavGraph(
            navController,
            onBack,
            navigate
        )
        if (withBottomBar) {
            NavigationBottomBar(
                currentRoute = currentRoute ?: ScreenRoute.Home.route,
                list = listOf(
                    TypeBottomNavigation(
                        key = ScreenRoute.Home.route,
                        icon = R.drawable.nav_home,
                        onClick = { navigate(ScreenRoute.Home, null) }
                    ),
                    TypeBottomNavigation(
                        key = ScreenRoute.Menu.route,
                        icon = R.drawable.nav_menu,
                        onClick = { navigate(ScreenRoute.Menu, null) }
                    ),
                    TypeBottomNavigation(
                        key = ScreenRoute.Statistic.route,
                        icon = R.drawable.nav_stat,
                        onClick = { navigate(ScreenRoute.Statistic, null) }
                    ),
                    TypeBottomNavigation(
                        key = ScreenRoute.Settings.route,
                        icon = R.drawable.nav_set,
                        onClick = { navigate(ScreenRoute.Settings, null) }
                    ),
                )
            )

        }
    }
}

@Composable
private fun NavGraph(
    navController: NavHostController,
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute, ScreenRoute?) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Home,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable<ScreenRoute.Home> {
            HomeScreen(
                navigateToTrainings = { navigateTo(ScreenRoute.Training, null) },
                navigateToTrainingPage = { id, date ->
                    navigateTo(
                        ScreenRoute.TrainingPage(
                            id,
                            date
                        ), null
                    )
                },
                navigateToMeasures = { navigateTo(ScreenRoute.Proportions, null) },
                navigateToNotes = { navigateTo(ScreenRoute.Notes, null) },
                navigateToHealth = { navigateTo(ScreenRoute.WatchData, null) },
                navigateToMeasurePage = { id, date ->
                    navigateTo(
                        ScreenRoute.ProportionPage(
                            id,
                            date
                        ), null
                    )
                },
            )
        }
        composable<ScreenRoute.Notes> {
            NotesScreen(
                onBackClick = navigateToBackStack
            )
        }

        composable<ScreenRoute.Menu> {
            MenuScreen(
                onBackClick = navigateToBackStack,
                navigateToTrainings = { navigateTo(ScreenRoute.Training, null) },
                navigateToMeasures = { navigateTo(ScreenRoute.Proportions, null) },
                navigateToCategories = { navigateTo(ScreenRoute.Categories, null) },
                navigateToExercises = { navigateTo(ScreenRoute.SportExercises, null) },
                navigateToBackup = { navigateTo(ScreenRoute.Backup, null) },
                navigateToCalculate = { navigateTo(ScreenRoute.Calculate, null) },
                navigateToWatchData = { navigateTo(ScreenRoute.WatchData, null) }
            )
        }

        composable<ScreenRoute.Statistic> {
            StatisticScreen(
                onBackClick = navigateToBackStack,
                navigateToStatisticDiagram = { navigateTo(ScreenRoute.StatisticDiagram, null) }
            )
        }
        composable<ScreenRoute.StatisticDiagram> {
            StatisticDiagramScreen(
                onBackClick = navigateToBackStack
            )
        }

        composable<ScreenRoute.Settings> {
            SettingsScreen(onBackClick = navigateToBackStack)
        }

        composable<ScreenRoute.Training> {
            TrainingsScreen(
                onBackClick = navigateToBackStack,
                onTrainingClick = { id, key ->
                    navigateTo(
                        ScreenRoute.TrainingPage(
                            id = id,
                            date = null,
                            key = key
                        ), null
                    )
                }
            )
        }
        composable<ScreenRoute.TrainingPage> { entry ->
            val args = entry.toRoute<ScreenRoute.TrainingPage>()

            TrainingPageScreen(
                id = args.id,
                key = args.key,
                date = args.date,
                onBackClick = navigateToBackStack
            )
        }

        composable<ScreenRoute.Categories> {
            CategoriesScreen(
                onBackClick = navigateToBackStack,
                onCategoryClick = { id -> navigateTo(ScreenRoute.CategoryPage(id), null) }
            )
        }
        composable<ScreenRoute.CategoryPage> { entry ->
            val args = entry.toRoute<ScreenRoute.CategoryPage>()

            RoutinePageScreen(
                id = args.id,
                key = args.key,
                onBackClick = navigateToBackStack
            )
        }

        composable<ScreenRoute.Proportions> {
            ProportionsScreen(
                onBackClick = navigateToBackStack,
                onProportionClick = { id -> navigateTo(ScreenRoute.ProportionPage(id), null) },
            )
        }
        composable<ScreenRoute.ProportionPage> { entry ->
            val args = entry.toRoute<ScreenRoute.ProportionPage>()

            ProportionPageScreen(
                id = args.id,
                onBackClick = navigateToBackStack,
            )
        }

        composable<ScreenRoute.SportExercises> {
            ExercisesScreen(
                onBackClick = navigateToBackStack,
                onEditClick = { id -> navigateTo(ScreenRoute.ExerciseEdit(id), null) },
                onExerciseClick = { id -> navigateTo(ScreenRoute.ExercisePage(id), null) }
            )
        }
        composable<ScreenRoute.ExercisePage> { entry ->
            val args = entry.toRoute<ScreenRoute.ExercisePage>()

            ExercisePageScreen(
                id = args.id,
                onBackClick = navigateToBackStack,
                navigateToEdit = { id -> navigateTo(ScreenRoute.ExerciseEdit(id), null) }
            )
        }
        composable<ScreenRoute.ExerciseEdit> { entry ->
            val args = entry.toRoute<ScreenRoute.ExerciseEdit>()

            ExerciseEditScreen(
                id = args.id,
                onBackClick = navigateToBackStack,
                navigateToExercisePage = { id ->
                    navigateTo(
                        ScreenRoute.ExercisePage(id),
                        ScreenRoute.ExerciseEdit(id)
                    )
                }
            )
        }

        composable<ScreenRoute.Backup> {
            BackupScreen(
                onBackClick = navigateToBackStack,
            )
        }
        composable<ScreenRoute.Calculate> {
            CalculatorScreen(
                onBackClick = navigateToBackStack
            )
        }
        composable<ScreenRoute.WatchData> {
            HealthDataScreen(
                onBackClick = navigateToBackStack
            )
        }
    }
}




