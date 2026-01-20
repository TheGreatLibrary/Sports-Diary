package com.sinya.projects.sportsdiary.presentation.categories

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.ui.features.SwipeCard
import com.sinya.projects.sportsdiary.ui.features.dialog.DeleteDialogView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onCategoryClick: (Int?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when(state) {
        CategoriesUiState.Loading -> PlaceholderScreen()

        is CategoriesUiState.Success -> CategoriesScreenView(
            state = state as CategoriesUiState.Success,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            onCategoryClick = onCategoryClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriesScreenView(
    state: CategoriesUiState.Success,
    onEvent: (CategoriesEvent) -> Unit,
    onBackClick: () -> Unit,
    onCategoryClick: (Int?) -> Unit,
) {
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage, state.isRefreshing) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(CategoriesEvent.OnErrorShown)
        }
        if (!state.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
        LaunchedEffect(Unit) {
            onEvent(CategoriesEvent.ReloadData)
        }
    }

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 50.dp)
        ) {
            item {
                NavigationTopBar(
                    type = TypeAppTopNavigation.WithIcon(
                        onBackClick = onBackClick,
                        title = stringResource(R.string.categories_title),
                        painter = R.drawable.ic_plus,
                        onClick = { onCategoryClick(null) }
                    )
                )
                Spacer(Modifier.height(20.dp))
            }

            items(
                items = state.categories,
                key = { it.id }
            ) {
                SwipeCard(
                    modifier = Modifier.padding(bottom = 8.dp),
                    id = it.id,
                    title = context.getString(R.string.category_with_name, it.name),
                    onTrainingClick = { onCategoryClick(it.id) },
                    onDelete = { id -> onEvent(CategoriesEvent.OpenDialog(id)) },
                )
            }
        }

        state.deleteDialogId?.let {
            GuideDialog(
                onDismiss = { onEvent(CategoriesEvent.OpenDialog(null)) },
                content = {
                    DeleteDialogView(
                        onSuccess = { onEvent(CategoriesEvent.DeleteCategory) },
                        onBack = { onEvent(CategoriesEvent.OpenDialog(null)) }
                    )
                }
            )
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}