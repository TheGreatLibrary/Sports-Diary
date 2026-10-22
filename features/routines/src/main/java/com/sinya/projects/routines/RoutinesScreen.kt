package com.sinya.projects.routines

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import com.sinya.projects.routines.components.CategoriesPlaceholder
import com.sinya.projects.ui.components.PopupMenu
import com.sinya.projects.ui.components.dragToSelect.dragToSelect
import com.sinya.projects.ui.components.screen.ScreenLazySmartHeader
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.DeleteSheetContent
import com.sinya.projects.ui.components.smartHeader.util.rememberSmartHeaderManager
import com.sinya.projects.ui.components.swipeCard.SwipeCard
import com.sinya.projects.ui.components.CustomSnackbar

@Composable
fun CategoriesScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (Int?) -> Unit
) {
    val viewModel: RoutinesViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        RoutinesUiState.Loading -> CategoriesPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(com.sinya.projects.ui.R.string.routines_title),
        )

        is RoutinesUiState.Success -> CategoriesScreenView(
            state = state as RoutinesUiState.Success,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            onCategoryClick = onCategoryClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriesScreenView(
    state: RoutinesUiState.Success,
    onEvent: (RoutinesEvent) -> Unit,
    onBackClick: () -> Unit,
    onCategoryClick: (Int?) -> Unit,
) {
    LocalContext.current
    val density = LocalDensity.current

    val pullToRefreshState = rememberPullToRefreshState()
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()

    val snackbarHostState = remember { SnackbarHostState() }
    var deleteState by remember { mutableStateOf(false) }
    var selectionState by remember { mutableStateOf(false) }

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
            onEvent(RoutinesEvent.OnErrorShown)
        }
    }

    BackHandler(enabled = selectionState) {
        selectionState = false
        onEvent(RoutinesEvent.ClearedListToDelete)
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(RoutinesEvent.ReloadData) },
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
                            true -> onEvent(RoutinesEvent.SelectProportion(id))
                        }
                    },
                    onHoverItem = { id ->
                        onEvent(RoutinesEvent.SelectProportion(id))
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
                            onEvent(RoutinesEvent.ClearedListToDelete)
                        }

                        else -> onBackClick()
                    }
                },
                title = stringResource(com.sinya.projects.ui.R.string.routines_title),
                painter = if (selectionState) com.sinya.projects.ui.R.drawable.morn_trash else com.sinya.projects.ui.R.drawable.ic_plus,
                onClick = {
                    if (selectionState) deleteState = true
                    else onCategoryClick(null)
                }
            ),
        ) {

            items(
                items = state.categories,
                key = { it.id }
            ) {
                var isMenuExpanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    SwipeCard(
                        modifier = Modifier,
                        title = stringResource(com.sinya.projects.ui.R.string.category_with_name, it.name),
                        description = if (state.currentPlanId == it.id) stringResource(R.string.plan_morning) else "",
                        swipeToDelete = {
                            onEvent(RoutinesEvent.SelectProportion(it.id))
                            deleteState = true
                        },
                        onClick = {
                            if (selectionState) onEvent(RoutinesEvent.SelectProportion(it.id))
                            else onCategoryClick(it.id)
                        },
                        swipeToChange = { onEvent(RoutinesEvent.OnPlanClick(it.id)) },
                        selected = it.selected,
                        icon = null,
                        selectionState = selectionState,
                        onMenuClick = { isMenuExpanded = true },
                        onSelected = { onEvent(RoutinesEvent.SelectProportion(it.id)) },
                        startIconBack = com.sinya.projects.ui.R.drawable.back_export,
                        endIconBack = com.sinya.projects.ui.R.drawable.morn_trash,
                    )

                    PopupMenu(
                        isMenuExpanded = isMenuExpanded,
                        onMenuExpanded = { isMenuExpanded = it },
                        item = it,
                        listItems = listOf(
                            PopupItem(
                                title = stringResource(com.sinya.projects.ui.R.string.open),
                                onClick = { onCategoryClick(it.id) },
                                icon = com.sinya.projects.ui.R.drawable.back_import
                            ),
                            PopupItem(
                                title = stringResource(com.sinya.projects.ui.R.string.analyze),
                                onClick = { onCategoryClick(it.id) },
                                icon = com.sinya.projects.ui.R.drawable.nav_stat
                            ),
                            PopupItem(
                                title = stringResource(R.string.plan_morning),
                                onClick = { onEvent(RoutinesEvent.OnPlanClick(it.id)) },
                                icon = com.sinya.projects.ui.R.drawable.back_export
                            ),
                            PopupItem(
                                title = stringResource(com.sinya.projects.ui.R.string.delete),
                                onClick = {
                                    onEvent(RoutinesEvent.SelectProportion(it.id))
                                    deleteState = true
                                },
                                icon = com.sinya.projects.ui.R.drawable.morn_trash
                            ),
                        )
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        CustomSnackbar(snackbarHostState)

        if (state.deleteItems.isNotEmpty() && deleteState) {
            CustomModalSheet(
                onDismiss = { deleteState = false },
                content = {
                    DeleteSheetContent(
                        onSuccess = {
                            onEvent(RoutinesEvent.DeleteCategory)
                            deleteState = false
                        },
                    )
                }
            )
        }
    }
}