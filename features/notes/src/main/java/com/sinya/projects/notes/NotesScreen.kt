package com.sinya.projects.notes

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
import com.sinya.projects.notes.components.NoteCard
import com.sinya.projects.ui.R
import com.sinya.projects.ui.R.string
import com.sinya.projects.ui.components.PopupMenu
import com.sinya.projects.ui.components.dragToSelect.dragToSelect
import com.sinya.projects.ui.components.screen.ScreenLazySmartHeader
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.sheet.components.DeleteSheetContent
import com.sinya.projects.ui.components.sheet.components.SortSheetContent
import com.sinya.projects.ui.components.smartHeader.util.rememberSmartHeaderManager
import com.sinya.projects.ui.components.CustomSnackbar
import com.sinya.projects.ui.theme.LocalDateFormatDMY
import com.sinya.projects.ui.theme.LocalDateFormatMY
import java.time.LocalDate
import java.time.YearMonth
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun NotesScreen(
    onBackClick: () -> Unit,
) {
    val viewModel: NotesViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        NotesUiState.Loading -> {}

        is NotesUiState.Success -> NotesView(
            state = state as NotesUiState.Success,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun NotesView(
    state: NotesUiState.Success,
    onEvent: (NotesEvent) -> Unit,
    onBackClick: () -> Unit
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
    val categories = remember(state.notes, state.selectedMode) {
        state.selectedMode.categories<Any, Any>(state.notes, context)
    }
    val grouped = remember(state.notes, state.selectedMode) {
        state.selectedMode.filter(state.notes).groupBy {
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
            onEvent(NotesEvent.OnErrorShown)
        }
    }

    BackHandler(enabled = selectionState) {
        selectionState = false
        onEvent(NotesEvent.ClearedListToDelete)
    }


    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(NotesEvent.ReloadData) },
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
                            true -> onEvent(NotesEvent.SelectNote(id))
                        }
                    },
                    onHoverItem = { id ->
                        onEvent(NotesEvent.SelectNote(id))
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
                            onEvent(NotesEvent.ClearedListToDelete)
                        }

                        else -> onBackClick()
                    }
                },
                title = stringResource(string.proportions_title),
                painter = if (selectionState) R.drawable.morn_trash else R.drawable.ic_eye,
                onClick = {
                    if (selectionState) { deleteState = true ; selectionState = false }
                    else { selectionState = true ; deleteState = false }
                }
            ),
            onOptionSelected = { sortState = true }
        ) {
            grouped.forEach { (month, note) ->
                item(key = "header_${month}") {
                    Text(
                        text = month.format(dateFormat).toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                items(
                    items = note,
                    key = { it.id }
                ) { item ->
                    var isMenuExpanded by remember { mutableStateOf(false) }
//                    val dateText = remember(item.date) { item.localDateOrNull()?.format(dateFmt) ?: item.date }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        NoteCard(
                            modifier = Modifier,
                            note = item.note,
                            edit = item.edit,
                            swipeToDelete = {
                                onEvent(NotesEvent.SelectNote(item.id))
                                deleteState = true
                            },
                            selected = item.selected,
                            selectionState = selectionState,
                            onMenuClick = { isMenuExpanded = true },
                            onSelect = { onEvent(NotesEvent.SelectNote(item.id)) },
                            startIconBack = null,
                            endIconBack = R.drawable.morn_trash,
                            swipeToEdit = { onEvent(NotesEvent.SelectEditMode(item.id)) },
                            onValueChange = { value -> onEvent(NotesEvent.OnValueChange(item.id, value)) },
                        )

                        PopupMenu(
                            isMenuExpanded = isMenuExpanded,
                            onMenuExpanded = { isMenuExpanded = it },
                            item = item,
                            listItems = listOf(
                                PopupItem(
                                    title = stringResource(string.edit),
                                    onClick = { onEvent(NotesEvent.SelectEditMode(item.id)) },
                                    icon = R.drawable.icon_edit
                                ),
                                PopupItem(
                                    title = stringResource(string.delete),
                                    onClick = {
                                        onEvent(NotesEvent.SelectNote(item.id))
                                        deleteState = true
                                    },
                                    icon = R.drawable.morn_trash
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
                                    onEvent(NotesEvent.SortParamChange(value))
                                },
                                categories = categories
                            )
                        }

                        deleteState -> {
                            DeleteSheetContent(
                                onSuccess = {
                                    onEvent(NotesEvent.DeleteNotes)
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