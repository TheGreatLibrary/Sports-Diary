package com.sinya.projects.sportsdiary.presentation.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.presentation.categories.components.CategoriesPlaceholder
import com.sinya.projects.sportsdiary.ui.features.CustomSnackbar
import com.sinya.projects.sportsdiary.ui.features.ScreenLazyColumn
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

    when (state) {
        CategoriesUiState.Loading -> CategoriesPlaceholder(
            onBackClick = onBackClick,
            title = stringResource(R.string.categories_title),
        )

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
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onEvent(CategoriesEvent.OnErrorShown)
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(CategoriesEvent.ReloadData) },
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        ScreenLazyColumn(
            arrangement = Arrangement.spacedBy(8.dp),
            navigationType = TypeTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.categories_title),
                painter = R.drawable.ic_plus,
                onClick = { onCategoryClick(null) }
            )
        ) {
            items(
                items = state.categories,
                key = { it.id }
            ) {
                SwipeCard(
                    modifier = Modifier,
                    id = it.id,
                    title = stringResource(R.string.category_with_name, it.name),
                    description = if (state.currentPlanId==it.id) stringResource(R.string.plan_morning) else "",
                    onTrainingClick = { onCategoryClick(it.id) },
                    onChange = { onEvent(CategoriesEvent.OnPlanClick(it.id)) },
                    onDelete = { id -> onEvent(CategoriesEvent.OpenDialog(id)) },
                )
            }

            item {
                Spacer(Modifier.height(80.dp))
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

        CustomSnackbar(snackbarHostState)
    }
}