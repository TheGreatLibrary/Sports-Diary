package com.sinya.projects.sportsdiary.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
import com.sinya.projects.sportsdiary.presentation.categories.CategoriesScreen
import com.sinya.projects.sportsdiary.presentation.categoryPage.CategoryPageScreen
import com.sinya.projects.sportsdiary.presentation.exerciseEdit.ExerciseEditScreen
import com.sinya.projects.sportsdiary.presentation.exercisePage.ExercisePageScreen
import com.sinya.projects.sportsdiary.presentation.exercises.ExercisesScreen
import com.sinya.projects.sportsdiary.presentation.home.HomeScreen
import com.sinya.projects.sportsdiary.presentation.menu.MenuScreen
import com.sinya.projects.sportsdiary.presentation.morningExercises.MorningExercisesScreen
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionPageScreen
import com.sinya.projects.sportsdiary.presentation.proportions.ProportionsScreen
import com.sinya.projects.sportsdiary.presentation.settings.SettingsScreen
import com.sinya.projects.sportsdiary.presentation.statistic.StatisticScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.TrainingPageScreen
import com.sinya.projects.sportsdiary.presentation.trainings.TrainingsScreen

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.simpleName

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
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavGraph(
            navController,
            onBack,
            navigate
        )
        if (withBottomBar) {
            NavigationBottomBar(
                currentRoute = currentRoute,
                navigateTo = navigate,
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
                navigateTo = { route -> navigateTo(route, null) }
            )
        }
        composable<ScreenRoute.Menu> {
            MenuScreen(
                onBackClick = navigateToBackStack,
                navigateTo = { route -> navigateTo(route, null) }
            )
        }
        composable<ScreenRoute.Statistic> {
            StatisticScreen(
                onBackClick = navigateToBackStack,
            )
        }
        composable<ScreenRoute.Settings> {
            SettingsScreen(onBackClick = navigateToBackStack)
        }

        composable<ScreenRoute.Training> {
            TrainingsScreen(
                onBackClick = navigateToBackStack,
                onTrainingClick = { id -> navigateTo(ScreenRoute.TrainingPage(id), null) }
            )
        }
        composable<ScreenRoute.TrainingPage> { entry ->
            val args = entry.toRoute<ScreenRoute.TrainingPage>()

            TrainingPageScreen(
                id = args.id,
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

            CategoryPageScreen(
                id = args.id,
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

        composable<ScreenRoute.MorningExercises> {
            MorningExercisesScreen(
                onBackClick = navigateToBackStack
            )
        }

        composable<ScreenRoute.Calculate> {

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

        composable<ScreenRoute.StatisticProportions> {

        }
        composable<ScreenRoute.StatisticExercises> {

        }
    }
}


