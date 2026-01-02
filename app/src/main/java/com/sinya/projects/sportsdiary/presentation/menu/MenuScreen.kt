package com.sinya.projects.sportsdiary.presentation.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.main.ScreenRoute
import com.sinya.projects.sportsdiary.ui.features.BlockOfCards
import com.sinya.projects.sportsdiary.ui.features.ListCardItem

@Composable
fun MenuScreen(
    onBackClick: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 50.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        NavigationTopBar(
            type = TypeAppTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.menu_title)
            )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BlockOfCards(title = stringResource(R.string.main_block)) {
                ListCardItem(
                    onClick = { navigateTo(ScreenRoute.Training) },
                    title = stringResource(R.string.training_title),
                    description = stringResource(R.string.list_of_training)
                )
                ListCardItem(
                    onClick = { navigateTo(ScreenRoute.MorningExercises) },
                    title = stringResource(R.string.morning_exercises_title),
                    description = stringResource(R.string.statistic_and_note)
                )
                ListCardItem(
                    onClick = { navigateTo(ScreenRoute.Proportions) },
                    title = stringResource(R.string.proportions_title),
                    description = stringResource(R.string.list_of_proportions)
                )
            }
            BlockOfCards(title = stringResource(R.string.other_block)) {
                ListCardItem(
                    onClick ={ navigateTo(ScreenRoute.SportExercises) },
                    title = stringResource(R.string.sports_exercises_title),
                    description = stringResource(R.string.list_of_exercises)
                )
//                ListCardItem(
//                    onClick = { navigateTo(ScreenRoute.Calculate) },
//                    title = stringResource(R.string.calculate_title),
//                    description = stringResource(R.string.calculate_description)
//                )
            }
        }
    }
}