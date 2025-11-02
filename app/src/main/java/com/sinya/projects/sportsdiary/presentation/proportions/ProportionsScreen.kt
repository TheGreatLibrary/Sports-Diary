package com.sinya.projects.sportsdiary.presentation.proportions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.proportions.components.ProportionByTime
import com.sinya.projects.sportsdiary.presentation.trainings.TrainingEvent
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog

@Composable
fun ProportionsScreen(
    vm: ProportionsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProportionClick: (Int?) -> Unit
) {
    when (val state = vm.state.value) {
        is ProportionsUiState.Loading -> PlaceholderScreen()
        is ProportionsUiState.Success -> ProportionsScreenView(
            state = state,
            onEvent = vm::onEvent,
            onBackClick = onBackClick,
            onProportionClick = onProportionClick
        )
        is ProportionsUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun ProportionsScreenView(
    state: ProportionsUiState.Success,
    onBackClick: () -> Unit,
    onEvent: (ProportionsEvent) -> Unit,
    onProportionClick: (Int?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            title = stringResource(R.string.proportions_title),
            isVisibleBack = true,
            isVisibleSave = true,
            onBackClick = onBackClick,
            secondaryIcon = painterResource(R.drawable.ic_plus),
            onSecondaryClick = { onProportionClick(null) }
        )
        ProportionByTime(
            proportions = state.proportions,
            onTrainingClick = onProportionClick,
            onMinusClick = { id -> onEvent(ProportionsEvent.OpenDialog(id)) }
        )

        state.deleteDialogId?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(ProportionsEvent.OpenDialog(null))
                },
                content = {
                    DeleteDialogView(
                        onSuccess = { onEvent(ProportionsEvent.DeleteProportion) },
                        onBack = { onEvent(ProportionsEvent.OpenDialog(null)) }
                    )
                }
            )
        }
    }
}