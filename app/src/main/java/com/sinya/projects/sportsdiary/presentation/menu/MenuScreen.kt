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
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.ui.features.BlockOfCards
import com.sinya.projects.sportsdiary.ui.features.ColumnOfCard
import com.sinya.projects.sportsdiary.ui.features.AnimationCard

@Composable
fun MenuScreen(
    onBackClick: () -> Unit,
    onTrainingClick: () -> Unit,
    onMorningExercisesClick: () -> Unit,
    onProportionsClick: () -> Unit,
    onCalculateClick: () -> Unit,
    onSportsExercisesClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            title = stringResource(R.string.menu_title),
            isVisibleBack = true,
            onBackClick = onBackClick
        )
        BlockOfCards(
            title = stringResource(R.string.main_block)
        ) {
            AnimationCard(
                onClick = onTrainingClick
            ) {
                ColumnOfCard(
                    title = stringResource(R.string.training_title),
                    description = stringResource(R.string.list_of_training),
                )
            }
            AnimationCard(
                onClick = onMorningExercisesClick
            ) {
                ColumnOfCard(
                    title = stringResource(R.string.morning_exercises_title),
                    description = stringResource(R.string.statistic_and_note),
                )
            }
            AnimationCard(
                onClick = onProportionsClick
            ) {
                ColumnOfCard(
                    title = stringResource(R.string.proportions_title),
                    description = stringResource(R.string.list_of_proportions),
                )
            }
        }
        BlockOfCards(
            title = stringResource(R.string.other_block)
        ) {
//            AnimationCard(
//                onClick = onCalculateClick
//            ) {
//                ColumnOfCard(
//                    title = stringResource(R.string.calculate_title),
//                    description = stringResource(R.string.calculate_description),
//                )
//            }
            AnimationCard(
                onClick = onSportsExercisesClick
            ) {
                ColumnOfCard(
                    title = stringResource(R.string.sports_exercises_title),
                    description = stringResource(R.string.list_of_exercises),
                )
            }
        }
    }
}