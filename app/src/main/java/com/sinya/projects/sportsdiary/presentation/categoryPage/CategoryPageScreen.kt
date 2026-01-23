package com.sinya.projects.sportsdiary.presentation.categoryPage

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.domain.model.ExerciseItemWithoutList
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.categoryPage.components.CategorySheetContent
import com.sinya.projects.sportsdiary.presentation.categoryPage.components.DragOverlay
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi
import com.sinya.projects.sportsdiary.ui.features.CustomButton
import com.sinya.projects.sportsdiary.ui.features.CustomTextField
import com.sinya.projects.sportsdiary.ui.features.ScaffoldBottomSheet
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.SwipeCardContent
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.ui.features.rememberReorderState
import kotlinx.coroutines.launch

@Composable
fun CategoryPageScreen(
    id: Int?,
    onBackClick: () -> Unit
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: CategoryPageViewModel.Factory ->
            factory.create(id = id)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        CategoryPageUiState.Loading -> PlaceholderScreen()

        is CategoryPageUiState.CategoryForm -> CategoryPageView(
            state = state as CategoryPageUiState.CategoryForm,
            onEvent = viewModel::onEvent,
            filtered = viewModel.filtered(),
            onBackClick = onBackClick
        )

        is CategoryPageUiState.Error -> ErrorScreen((state as CategoryPageUiState.Error).errorMessage)

        CategoryPageUiState.Success -> {
            LaunchedEffect(Unit) {
                onBackClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPageView(
    state: CategoryPageUiState.CategoryForm,
    filtered: List<ExerciseUi>,
    onEvent: (CategoryPageEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val reorderState = rememberReorderState(
        listState = listState,
        items = state.item.items,
        scope = scope,
        idProvided = { it.id }
    ) { from, to -> onEvent(CategoryPageEvent.MoveExercise(from, to)) }


    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(CategoryPageEvent.OnErrorShown)
        }
    }

    ScaffoldBottomSheet(
        scaffoldState = scaffoldState,
        sheetContent = { CategorySheetContent(state, filtered, onEvent, scaffoldState) }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(Modifier.height(40.dp))
                    NavigationTopBar(
                        type = TypeAppTopNavigation.WithIcon(
                            onBackClick = onBackClick,
                            title = state.item.category.name,
                            painter = R.drawable.nav_save,
                            onClick = { onEvent(CategoryPageEvent.Save) }
                        )
                    )
                    Spacer(Modifier.height(20.dp))
                }

                item {
                    CustomTextField(
                        value = state.item.category.name,
                        onValueChange = { s -> onEvent(CategoryPageEvent.OnValueChange(s)) },
                        onTrailingClick = { onEvent(CategoryPageEvent.OnValueChange("")) },
                        placeholder = stringResource(R.string.put_your_title),
                        shape = MaterialTheme.shapes.extraLarge,
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.fillMaxWidth(),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        isError = state.isError,
                        errorMessage = stringResource(R.string.category_name_is_empty)
                    )
                    Spacer(Modifier.height(20.dp))
                }

                items(
                    items = state.item.items,
                    key = { it.id }
                ) { item ->

                    val isDragging = reorderState.draggedItemId == item.id

                    SwipeCard(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .alpha(if (isDragging) 0f else 1f)
                            .pointerInput(item.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { reorderState.onDragStart(item.id) },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        reorderState.onDrag(dragAmount.y)
                                    },
                                    onDragEnd = { reorderState.onDragEnd() },
                                    onDragCancel = { reorderState.onDragEnd() }
                                )
                            },
                        id = item.id,
                        title = item.title,
                        onTrainingClick = { onEvent(CategoryPageEvent.OpenDialog(item.id)) },
                        onDelete = { id -> onEvent(CategoryPageEvent.Delete(id)) },
                    )
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomButton(
                            onClick = {
                                scope.launch {
                                    onEvent(CategoryPageEvent.OpenBottomSheetTraining)
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            text = stringResource(R.string.add_exercise),
                        )
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }

            DragOverlay(
                reorderState = reorderState,
                index = state.item.items.indexOfFirst { it.id == reorderState.draggedItemId },
                listState = listState
            ) {
                SwipeCardContent(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = state.item.items.find { it.id == reorderState.draggedItemId }?.title ?: "",
                    description = null,
                    onTrainingClick = { }
                )
            }

            state.dialogContent?.let {
                GuideDialog(
                    onDismiss = {
                        onEvent(CategoryPageEvent.OpenDialog(null))
                    },
                    content = {
                        GuideDescriptionView(
                            title = it.name,
                            description = it.description,
                            image = null
                        )
                    }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}