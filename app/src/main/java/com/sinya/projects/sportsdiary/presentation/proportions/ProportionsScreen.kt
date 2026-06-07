package com.sinya.projects.sportsdiary.presentation.proportions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.localDateOrNull
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.proportions.components.ProportionsHeader
import com.sinya.projects.sportsdiary.presentation.trainings.dateFmt
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.ui.features.smartHeader.rememberSmartHeaderManager
import com.sinya.projects.sportsdiary.ui.features.smartHeader.smartHeader

@Composable
fun ProportionsScreen(
    viewModel: ProportionsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProportionClick: (Int?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ProportionsUiState.Loading -> PlaceholderScreen()

        is ProportionsUiState.Success -> ProportionsScreenView(
            state = state as ProportionsUiState.Success,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            onProportionClick = onProportionClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProportionsScreenView(
    state: ProportionsUiState.Success,
    onBackClick: () -> Unit,
    onEvent: (ProportionsEvent) -> Unit,
    onProportionClick: (Int?) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val pullToRefreshState = rememberPullToRefreshState()
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()
    val snackbarHostState = remember { SnackbarHostState() }

    val headerVisibleHeightDp = with(density) {
        (smartHeader.headerHeightPx + smartHeader.headerOffsetPx)
            .coerceAtLeast(0f)
            .toDp()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(ProportionsEvent.OnErrorShown)
        }
    }

    val grouped = remember(state.proportions, state.selectedMode) {
        state.selectedMode.filter(state.proportions)
    }

    val categories = remember(state.proportions, state.selectedMode) {
        state.selectedMode.categories<Any, Any>(state.proportions, context)
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(ProportionsEvent.ReloadData) },
        state = pullToRefreshState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .nestedScroll(smartHeader.headerScrollConnection),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier.padding(top = headerVisibleHeightDp),
            state = smartHeader.listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = grouped,
                key = { it.id }
            ) {
                val dateText = it.localDateOrNull()?.format(dateFmt) ?: it.date

                SwipeCard(
                    modifier = Modifier,
                    id = it.id,
                    title = stringResource(R.string.proportion_number, it.id),
                    description = dateText,
                    onTrainingClick = { onProportionClick(it.id) },
                    onDelete = { id -> onEvent(ProportionsEvent.OpenDialog(id)) },
                )
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }

        ProportionsHeader(
            modifier = Modifier.smartHeader(manager),
            onOptionSelected = { value ->
                onEvent(ProportionsEvent.SortParamChange(value))
            },
            navigationType = TypeAppTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.proportions_title),
                painter = R.drawable.ic_plus,
                onClick = { onProportionClick(null) }
            ),
            categories = categories
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}