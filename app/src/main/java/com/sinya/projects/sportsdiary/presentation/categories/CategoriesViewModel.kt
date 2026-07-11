package com.sinya.projects.sportsdiary.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.data.dataStore.SettingsEngine
import com.sinya.projects.sportsdiary.core.domain.useCase.DeleteCategoryUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.ObserveCategoriesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getCategoryListUseCase: ObserveCategoriesListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val state: StateFlow<CategoriesUiState> = _state.asStateFlow()

    init {
        loadData()
        observeSettings()
    }

    private fun observeSettings() = viewModelScope.launch {
        settingsEngine.uiState.collectLatest { config ->
            updateIfSuccess { it.copy(currentPlanId = config.planMorningId) }
        }
    }


    fun onEvent(event: CategoriesEvent) {
        when (event) {
            is CategoriesEvent.OpenDialog -> updateIfSuccess { it.copy(deleteDialogId = event.id) }

            CategoriesEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            CategoriesEvent.ReloadData -> loadData()

            CategoriesEvent.DeleteCategory -> delete()

            is CategoriesEvent.OnPlanClick -> {
                val state = state.value as? CategoriesUiState.Success ?: return
                if (state.currentPlanId == event.id) settingsEngine.setPlanMorningId(null)
                else settingsEngine.setPlanMorningId(event.id)
            }
        }
    }


    private fun delete() = viewModelScope.launch {
        val currentState = _state.value as? CategoriesUiState.Success ?: return@launch

        currentState.deleteDialogId?.let { id ->
            val item = currentState.categories.first { id == it.id }

            deleteCategoryUseCase(item).fold(
                onSuccess = {
                    if (item.id==currentState.currentPlanId) settingsEngine.setPlanMorningId(null)
                    updateIfSuccess {
                        it.copy(
                            deleteDialogId = null
                        )
                    }
                },
                onFailure = { error ->
                    updateIfSuccess { it.copy(errorMessage = error.toString()) }
                }
            )
        }
    }

    private fun loadData() = viewModelScope.launch {
        updateIfSuccess { it.copy(isRefreshing = true) }

        getCategoryListUseCase().collectLatest { list ->
            if (_state.value is CategoriesUiState.Success) {
                updateIfSuccess {
                    it.copy(
                        categories = list,
                        isRefreshing = false
                    )
                }
            } else {
                _state.value =
                    CategoriesUiState.Success(categories = list, isRefreshing = false)
            }
        }
    }

    private fun updateIfSuccess(transform: (CategoriesUiState.Success) -> CategoriesUiState.Success) {
        _state.update { currentState ->
            if (currentState is CategoriesUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}