package com.sinya.projects.trainings

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.model.PopupItem
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.domain.model.localDateOrNull
import com.sinya.projects.trainings.components.TrainingsPlaceholder
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.PopupMenu
import com.sinya.projects.ui.components.dragToSelect.dragToSelect
import com.sinya.projects.ui.components.screen.ScreenLazySmartHeader
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.DeleteSheetContent
import com.sinya.projects.ui.components.sheet.components.SortSheetContent
import com.sinya.projects.ui.components.smartHeader.util.rememberSmartHeaderManager
import com.sinya.projects.ui.components.swipeCard.SwipeCard
import com.sinya.projects.ui.components.CustomSnackbar
import com.sinya.projects.ui.theme.LocalDateFormatDMY
import com.sinya.projects.ui.theme.LocalDateFormatMY
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun TrainingsScreen(
    onBackClick: () -> Unit,
    onTrainingClick: (Int?, Int) -> Unit
) {
    val viewModel: TrainingViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        TrainingUiState.Loading -> TrainingsPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(R.string.trainings_title)
        )

        is TrainingUiState.Success -> {
            TrainingsScreenView(
                state = state as TrainingUiState.Success,
                onEvent = viewModel::onEvent,
                onBackClick = onBackClick,
                onTrainingClick = onTrainingClick
            )
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
private fun TrainingsScreenView(
    state: TrainingUiState.Success,
    onEvent: (TrainingEvent) -> Unit,
    onBackClick: () -> Unit,
    onTrainingClick: (Int?, Int) -> Unit,
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val dateFmt = LocalDateFormatDMY.current
    val dateFormat = LocalDateFormatMY.current

    val pullToRefreshState = rememberPullToRefreshState()
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()

    val snackbarHostState = remember { SnackbarHostState() }
    var sortState by remember { mutableStateOf(false) }
    var deleteState by remember { mutableStateOf(false) }
    var selectionState by remember { mutableStateOf(false) }

    val headerVisibleHeightDp = with(density) {
        (smartHeader.headerHeightPx + smartHeader.headerOffsetPx)
            .coerceAtLeast(0f)
            .toDp()
    }
    val categories = remember(state.trainings, state.selectedMode) {
        state.selectedMode.categories<Any, Any>(state.trainings, context)
    }
    val grouped = remember(state.trainings, state.selectedMode) {
        state.selectedMode.filter(state.trainings).groupBy {
            val date = LocalDate.parse(it.date)
            YearMonth.of(date.year, date.month)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(TrainingEvent.OnErrorShown)
        }
    }

    BackHandler(enabled = selectionState) {
        selectionState = false
        onEvent(TrainingEvent.ClearedListToDelete)
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(TrainingEvent.ReloadData) },
        state = pullToRefreshState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .nestedScroll(smartHeader.headerScrollConnection),
        contentAlignment = Alignment.TopCenter
    ) {
        ScreenLazySmartHeader(
            modifier = Modifier
                .padding(top = headerVisibleHeightDp)
                .dragToSelect(
                    lazyListState = smartHeader.listState,
                    onStartSelection = { id ->
                        when (selectionState) {
                            false -> selectionState = true
                            true -> onEvent(TrainingEvent.SelectTraining(id))
                        }
                    },
                    onHoverItem = { id ->
                        onEvent(TrainingEvent.SelectTraining(id))
                    }
                ),
            list = smartHeader.listState,
            arrangement = Arrangement.spacedBy(8.dp),
            manager = manager,
            navigationType = TypeTopNavigation.WithIcon(
                onBackClick = {
                    when (selectionState) {
                        true -> {
                            selectionState = false
                            onEvent(TrainingEvent.ClearedListToDelete)
                        }
                        else -> onBackClick()
                    }
                },
                title = stringResource(R.string.trainings_title),
                painter = if (selectionState) R.drawable.morn_trash else R.drawable.ic_plus,
                onClick = {
                    if (selectionState) deleteState = true
                    else onTrainingClick(null, 1)
                }
            ),
            onOptionSelected = { sortState = true }
        ) {
            grouped.forEach { (month, trainings) ->
                item(key = "header_${month}") {
                    Text(
                        text = month.format(dateFormat).toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                items(
                    items = trainings,
                    key = { it.id }
                ) { item ->
                    var isMenuExpanded by remember { mutableStateOf(false) }
                    val dateText = remember(item.date) { item.localDateOrNull()?.format(dateFmt) ?: item.date }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        SwipeCard(
                            modifier = Modifier,
                            title = context.getString(R.string.training_number, item.name),
                            description = "${item.category ?: context.getString(R.string.not_category)} • ${dateText}",
                            swipeToDelete = {
                                onEvent(TrainingEvent.SelectTraining(item.id))
                                deleteState = true
                            },
                            onClick = {
                                if (selectionState) onEvent(TrainingEvent.SelectTraining(item.id))
                                else onTrainingClick(item.id, 1)
                            },
                            swipeToChange = null,
                            selected = item.selected,
                            icon = null,
                            selectionState = selectionState,
                            onMenuClick = { isMenuExpanded = true },
                            onSelected = { onEvent(TrainingEvent.SelectTraining(item.id)) },
                            startIconBack = null,
                            endIconBack = R.drawable.morn_trash,
                        )

                        PopupMenu(
                            isMenuExpanded = isMenuExpanded,
                            onMenuExpanded = { isMenuExpanded = it },
                            item = item,
                            listItems = listOf(
                                PopupItem(
                                    title = stringResource(R.string.open),
                                    onClick = { onTrainingClick(it.id, 1) },
                                    icon = R.drawable.back_import
                                ),
                                PopupItem(
                                    title = stringResource(R.string.analyze),
                                    onClick = { onTrainingClick(it.id, 0) },
                                    icon = R.drawable.nav_stat
                                ),
                                PopupItem(
                                    title = stringResource(R.string.delete),
                                    onClick = {
                                        onEvent(TrainingEvent.SelectTraining(item.id))
                                        deleteState = true
                                    },
                                    icon = R.drawable.morn_trash
                                )
                            )
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        CustomSnackbar(snackbarHostState)

        if (sortState || (state.deleteItems.isNotEmpty() && deleteState)) {
            CustomModalSheet(
                onDismiss = {
                    when {
                        sortState -> sortState = false
                        deleteState -> deleteState = false
                    }
                },
                content = {
                    when {
                        sortState -> {
                            SortSheetContent(
                                selectedMode = state.selectedMode,
                                onOptionSelected = { onEvent(TrainingEvent.ModeChange(it)) },
                                onCategoryOptionSelected = { value ->
                                    onEvent(
                                        TrainingEvent.SortParamChange(value)
                                    )
                                },
                                categories = categories
                            )
                        }

                        deleteState -> {
                            DeleteSheetContent(
                                onSuccess = {
                                    onEvent(TrainingEvent.DeleteTraining)
                                    deleteState = false
                                },
                            )
                        }
                    }
                }
            )
        }
    }
}