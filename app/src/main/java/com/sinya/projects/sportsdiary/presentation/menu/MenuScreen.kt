package com.sinya.projects.sportsdiary.presentation.menu

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.navigation.ScreenRoute
import com.sinya.projects.sportsdiary.ui.features.BlockOfCards
import com.sinya.projects.sportsdiary.ui.features.ListCardItem
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn

@Composable
fun MenuScreen(
    onBackClick: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit
) {
    ScreenColumn(
        navigationType = TypeAppTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = stringResource(R.string.menu_title)
        )
    ) {
        BlockOfCards(title = stringResource(R.string.main_block)) {
            ListCardItem(
                onClick = { navigateTo(ScreenRoute.Training) },
                title = stringResource(R.string.trainings_title),
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
                onClick = { navigateTo(ScreenRoute.SportExercises) },
                title = stringResource(R.string.sports_exercises_title),
                description = stringResource(R.string.list_of_exercises)
            )
            ListCardItem(
                onClick = { navigateTo(ScreenRoute.Categories) },
                title = stringResource(R.string.categories_title),
                description = stringResource(R.string.categories_description)
            )
//                ListCardItem(
//                    onClick = { navigateTo(ScreenRoute.Calculate) },
//                    title = stringResource(R.string.calculate_title),
//                    description = stringResource(R.string.calculate_description)
//                )
        }
        Spacer(Modifier.height(80.dp))
    }
}