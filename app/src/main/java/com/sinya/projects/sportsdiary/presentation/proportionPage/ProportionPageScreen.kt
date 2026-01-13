package com.sinya.projects.sportsdiary.presentation.proportionPage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.domain.model.buildMeasureItemsFromKeys
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.proportionPage.components.MeasureCard
import com.sinya.projects.sportsdiary.ui.features.DateCard
import com.sinya.projects.sportsdiary.ui.features.DatePickerModal
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.utils.getDrawableId
import com.sinya.projects.sportsdiary.utils.getString

@Composable
fun ProportionPageScreen(
    id: Int?,
    onBackClick: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: ProportionPageViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ProportionPageUiState.Loading -> PlaceholderScreen()

        is ProportionPageUiState.ProportionForm -> ProportionPageView(
            onBackClick = onBackClick,
            state = state as ProportionPageUiState.ProportionForm,
            onEvent = viewModel::onEvent
        )

        is ProportionPageUiState.Error -> ErrorScreen((state as ProportionPageUiState.Error).errorMessage)

        ProportionPageUiState.Success -> {
            LaunchedEffect(Unit) {
                onBackClick()
            }
        }
    }
}

@Composable
private fun ProportionPageView(
    state: ProportionPageUiState.ProportionForm,
    onBackClick: () -> Unit,
    onEvent: (ProportionPageEvent) -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val rows = remember(state.item) { state.item.items.buildMeasureItemsFromKeys() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ProportionPageEvent.OnErrorShown)
        }
    }

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 50.dp, end = 16.dp),
        ) {
            item {
                NavigationTopBar(
                    type = TypeAppTopNavigation.WithIcon(
                        onBackClick = onBackClick,
                        title = if (state.item.title.isNotEmpty()) stringResource(R.string.proportion_number, state.item.title)
                                else stringResource(R.string.measurement),
                        painter = R.drawable.nav_save,
                        onClick = { onEvent(ProportionPageEvent.Save) }
                    )
                )
                Spacer(Modifier.height(20.dp))
                DateCard(
                    onDateClick = { onEvent(ProportionPageEvent.CalendarState(true)) },
                    date = state.item.date
                )
                Spacer(Modifier.height(20.dp))
            }

            items(
                items = rows,
                key = { it.headerKey }
            ) { item ->
                MeasureCard(
                    title = context.getString(item.headerKey),
                    onInfoClick = { onEvent(ProportionPageEvent.OpenDialog(item.infoId)) },
                    items = item.items,
                    onValueChange = { id, str -> onEvent(ProportionPageEvent.OnChangeValue(id, str)) },
                    context = context
                )
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }

        state.dialogContent?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(ProportionPageEvent.OpenDialog(null))
                },
                content = {
                    GuideDescriptionView(
                        title = it.name,
                        description = it.description,
                        image = if (!it.icon.isNullOrEmpty()) painterResource(
                            context.getDrawableId(
                                it.icon
                            )
                        ) else null
                    )
                }
            )
        }

        if (state.calendarVisible) {
            DatePickerModal(
                onDateSelected = { date -> onEvent(ProportionPageEvent.OnPickDate(date)) },
                onDismiss = { onEvent(ProportionPageEvent.CalendarState(false)) }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}
