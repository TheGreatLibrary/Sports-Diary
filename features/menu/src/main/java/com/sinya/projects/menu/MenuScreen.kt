package com.sinya.projects.menu

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.CardItem
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.BlockOfCards
import com.sinya.projects.ui.components.screen.ScreenColumn


@Composable
fun MenuScreen(
    onBackClick: () -> Unit,
    navigateToTrainings: () -> Unit,
    navigateToMeasures: () -> Unit,
    navigateToCategories: () -> Unit,
    navigateToExercises: () -> Unit,
    navigateToBackup: () -> Unit,
    navigateToCalculate: () -> Unit,
    navigateToWatchData: () -> Unit
) {
    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = stringResource(R.string.menu_title)
        )
    ) {
        BlockOfCards(
            title = stringResource(R.string.menu_main),
            list = listOf(
                CardItem(
                    stringResource(com.sinya.projects.ui.R.string.trainings_title),
                    R.drawable.menu_trains,
                    navigateToTrainings
                ),
                CardItem(
                    stringResource(com.sinya.projects.ui.R.string.proportions_title),
                    R.drawable.menu_measures,
                    navigateToMeasures
                ),
                CardItem(
                    stringResource(R.string.menu_routines),
                    R.drawable.menu_routines,
                    navigateToCategories
                ),
                CardItem(
                    stringResource(com.sinya.projects.ui.R.string.sports_exercises_title),
                    R.drawable.menu_exers,
                    navigateToExercises
                )
            )
        )
        BlockOfCards(
            title = stringResource(R.string.menu_other),
            list = listOf(
                CardItem(
                    stringResource(com.sinya.projects.ui.R.string.backup_title),
                    R.drawable.menu_backup,
                    navigateToBackup
                ),
                CardItem(
                    stringResource(com.sinya.projects.ui.R.string.calculate_title),
                    R.drawable.menu_calculator,
                    navigateToCalculate
                ),
                CardItem(
                    stringResource(com.sinya.projects.ui.R.string.watch_data),
                    R.drawable.menu_watch,
                    navigateToWatchData
                )
            )
        )
        Spacer(Modifier.height(80.dp))
    }
}