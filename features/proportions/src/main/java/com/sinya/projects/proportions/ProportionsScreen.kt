package com.sinya.projects.proportions

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
import com.sinya.projects.proportions.components.ProportionsPlaceholder
import com.sinya.projects.ui.R.string
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
fun ProportionsScreen(
    onBackClick: () -> Unit,
    onProportionClick: (Int?) -> Unit
) {
    val viewModel: ProportionsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        ProportionsUiState.Loading -> ProportionsPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(string.proportions_title)
        )

        is ProportionsUiState.Success -> {
            ProportionsScreenView(
                state = state as ProportionsUiState.Success,
                onEvent = viewModel::onEvent,
                onBackClick = onBackClick,
                onProportionClick = onProportionClick
            )
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
private fun ProportionsScreenView(
    state: ProportionsUiState.Success,
    onBackClick: () -> Unit,
    onEvent: (ProportionsEvent) -> Unit,
    onProportionClick: (Int?) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
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
    val categories = remember(state.proportions, state.selectedMode) {
        state.selectedMode.categories<Any, Any>(state.proportions, context)
    }
    val grouped = remember(state.proportions, state.selectedMode) {
        state.selectedMode.filter(state.proportions).groupBy {
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
            onEvent(ProportionsEvent.OnErrorShown)
        }
    }

    BackHandler(enabled = selectionState) {
        selectionState = false
        onEvent(ProportionsEvent.ClearedListToDelete)
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
        ScreenLazySmartHeader(
            modifier = Modifier
                .padding(top = headerVisibleHeightDp)
                .dragToSelect(
                    lazyListState = smartHeader.listState,
                    onStartSelection = { id ->
                        when (selectionState) {
                            false -> selectionState = true
                            true -> onEvent(ProportionsEvent.SelectProportion(id))
                        }
                    },
                    onHoverItem = { id ->
                        onEvent(ProportionsEvent.SelectProportion(id))
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
                            onEvent(ProportionsEvent.ClearedListToDelete)
                        }
                        else -> onBackClick()
                    }
                },
                title = stringResource(string.proportions_title),
                painter = if (selectionState) com.sinya.projects.ui.R.drawable.morn_trash else com.sinya.projects.ui.R.drawable.ic_plus,
                onClick = {
                    if (selectionState) deleteState = true
                    else onProportionClick(null)
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
                            title = context.getString(
                                string.proportion_number,
                                item.id.toString()
                            ),
                            description = dateText,
                            swipeToDelete = {
                                onEvent(ProportionsEvent.SelectProportion(item.id))
                                deleteState = true
                            },
                            onClick = {
                                if (selectionState) onEvent(ProportionsEvent.SelectProportion(item.id))
                                else onProportionClick(item.id)
                            },
                            swipeToChange = null,
                            selected = item.selected,
                            icon = null,
                            selectionState = selectionState,
                            onMenuClick = { isMenuExpanded = true },
                            onSelected = { onEvent(ProportionsEvent.SelectProportion(item.id)) },
                            startIconBack = null,
                            endIconBack = com.sinya.projects.ui.R.drawable.morn_trash,
                        )

                        PopupMenu(
                            isMenuExpanded = isMenuExpanded,
                            onMenuExpanded = { isMenuExpanded = it },
                            item = item,
                            listItems = listOf(
                                PopupItem(
                                    title = stringResource(string.open),
                                    onClick = { onProportionClick(it.id) },
                                    icon = com.sinya.projects.ui.R.drawable.back_import
                                ),
                                PopupItem(
                                    title = stringResource(string.delete),
                                    onClick = {
                                        onEvent(ProportionsEvent.SelectProportion(item.id))
                                        deleteState = true
                                    },
                                    icon = com.sinya.projects.ui.R.drawable.morn_trash
                                ),
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
                                onOptionSelected = null,
                                onCategoryOptionSelected = { value ->
                                    onEvent(
                                        ProportionsEvent.SortParamChange(value)
                                    )
                                },
                                categories = categories
                            )
                        }

                        deleteState -> {
                            DeleteSheetContent(
                                onSuccess = {
                                    onEvent(ProportionsEvent.DeleteProportion)
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