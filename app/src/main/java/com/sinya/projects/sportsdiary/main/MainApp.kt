package com.sinya.projects.sportsdiary.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sinya.projects.sportsdiary.data.datastore.AppViewModel
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
import com.sinya.projects.sportsdiary.ui.features.getCurrentRoute
import com.sinya.projects.sportsdiary.ui.theme.SportsDiaryTheme
import com.sinya.projects.sportsdiary.utils.updateLocale

@Composable
fun MainApp(
    viewModel: AppViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val currentPlanId by viewModel.planId.collectAsState()


    context.updateLocale(language)
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
        { route: ScreenRoute, lastRoute: ScreenRoute? ->
            navController.navigate(route) {
                launchSingleTop = true
                if (lastRoute!=null) popUpTo(lastRoute::class) {
                    inclusive = true
                }
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
                    onBack,
                    navigate,
                    Modifier.padding(paddingValue)
                )
            }
        }
    }
}

@Composable
private fun NavGraph(
    navController: NavHostController,
    currentPlanId: Int?,
    onBack: () -> Unit,
    navigateTo: (ScreenRoute, ScreenRoute?) -> Unit,
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
                navigateTo = { route -> navigateTo(route, null) }
            )
        }
        composable<ScreenRoute.Menu> {
            MenuScreen(
                onBackClick = onBack,
                navigateTo = { route -> navigateTo(route, null) }
            )
        }
        composable<ScreenRoute.Statistic> {
            StatisticScreen(
                onBackClick = onBack,
            )
        }
        composable<ScreenRoute.Settings> {
            SettingsScreen(onBackClick = onBack)
        }

        composable<ScreenRoute.Training> {
            TrainingsScreen(
                onBackClick = onBack,
                onTrainingClick = { id -> navigateTo(ScreenRoute.TrainingPage(id), null) }
            )
        }
        composable<ScreenRoute.TrainingPage> { entry ->
            val args = entry.toRoute<ScreenRoute.TrainingPage>()

            TrainingPageScreen(
                id = args.id,
                onBackClick = onBack
            )
        }

        composable<ScreenRoute.Categories> {
            CategoriesScreen(
                onBackClick = onBack,
                onCategoryClick = { id -> navigateTo(ScreenRoute.CategoryPage(id), null) }
            )
        }
        composable<ScreenRoute.CategoryPage> { entry ->
            val args = entry.toRoute<ScreenRoute.CategoryPage>()

            CategoryPageScreen(
                id = args.id,
                onBackClick = onBack
            )
        }


        composable<ScreenRoute.Proportions> {
            ProportionsScreen(
                onBackClick = onBack,
                onProportionClick = { id -> navigateTo(ScreenRoute.ProportionPage(id), null) },
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
                onEditClick = { id -> navigateTo(ScreenRoute.ExerciseEdit(id), null) },
                onExerciseClick = { id -> navigateTo(ScreenRoute.ExercisePage(id), null) }
            )
        }
        composable<ScreenRoute.ExercisePage> { entry ->
            val args = entry.toRoute<ScreenRoute.ExercisePage>()

            ExercisePageScreen(
                id = args.id,
                onBackClick = onBack,
                navigateToEdit = { id -> navigateTo(ScreenRoute.ExerciseEdit(id), null) }
            )
        }
        composable<ScreenRoute.ExerciseEdit> { entry ->
            val args = entry.toRoute<ScreenRoute.ExerciseEdit>()

            ExerciseEditScreen(
                id = args.id,
                onBackClick = onBack,
                navigateToExercisePage = { id -> navigateTo(ScreenRoute.ExercisePage(id), ScreenRoute.ExerciseEdit(id)) }
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



