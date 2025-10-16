package com.sinya.projects.sportsdiary.presentation.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.statistic.components.RadioButtons
import com.sinya.projects.sportsdiary.presentation.statistic.components.StatCard
import com.sinya.projects.sportsdiary.ui.features.diagram.Chart

@Composable
fun StatisticScreen(
    onBackClick: () -> Unit,
    vm: StatisticScreenViewModel = hiltViewModel()
) {
    when (val state = vm.state.value) {
        is StatisticScreenUiState.Loading -> PlaceholderScreen()
        is StatisticScreenUiState.Success -> {
            StatisticScreenView(
                onBackClick = onBackClick,
                state = state,
                onEvent = vm::onEvent
            )
        }
        is StatisticScreenUiState.Error -> ErrorScreen(state.message)
    }
}


@Composable
fun StatisticScreenView(
    onBackClick: () -> Unit,
    state: StatisticScreenUiState.Success,
    onEvent: (StatisticScreenUiEvent) -> Unit,
    radioOptions: List<String> = listOf(
        stringResource(R.string.days),
        stringResource(R.string.months),
        stringResource(R.string.years)
    )
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            title = stringResource(R.string.statistic_title),
            isVisibleBack = true,
            onBackClick = onBackClick
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.count_of_training),
                count = state.countTrain.toString()
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.all_weight),
                count = state.countWeight.toString()
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RadioButtons(
                radioOptions = radioOptions,
                selectedOption = state.timeMode.index,
                onOptionSelected = { index -> onEvent(StatisticScreenUiEvent.OnSelectTimePeriod(index)) }
            )
            Chart(
                title = stringResource(R.string.effect_of_training),
                onInfoClick = { },
                timeMode = state.timeMode,
                points = state.chartList
            )
        }
    }
}