package com.sinya.projects.sportsdiary.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.domain.useCase.DeleteCategoryUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetCategoriesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getCategoryListUseCase: GetCategoriesListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val state: StateFlow<CategoriesUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: CategoriesEvent) {
        when (event) {
            is CategoriesEvent.OpenDialog -> updateIfSuccess { it.copy(deleteDialogId = event.id) }

            CategoriesEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }

            CategoriesEvent.ReloadData -> loadData()

            CategoriesEvent.DeleteCategory -> delete()
        }
    }

    private fun delete() = viewModelScope.launch {
        val currentState = _state.value as? CategoriesUiState.Success ?: return@launch

        currentState.deleteDialogId?.let { id ->
            if (id == 1) {
                val item = currentState.categories.first { id == it.id }

                deleteCategoryUseCase(item).fold(
                    onSuccess = {
                        updateIfSuccess { it.copy(deleteDialogId = null) }
                        loadData()
                    },
                    onFailure = { error ->
                        updateIfSuccess { it.copy(errorMessage = error.toString()) }
                    }
                )
            }
        }
    }

    private fun loadData() = viewModelScope.launch {
        updateIfSuccess { it.copy(isRefreshing = true) }

        getCategoryListUseCase().fold(
            onSuccess = { list ->
                if (_state.value is CategoriesUiState.Success) {
                    updateIfSuccess {
                        it.copy(
                            categories = list,
                            isRefreshing = false
                        )
                    }
                }
                else {
                    _state.value = CategoriesUiState.Success(categories = list, isRefreshing = false)
                }
            },
            onFailure = { error ->
                if (_state.value is CategoriesUiState.Success) {
                    updateIfSuccess {
                        it.copy(
                            errorMessage = error.toString(),
                            isRefreshing = false
                        )
                    }
                }
                else {
                    _state.value = CategoriesUiState.Success(
                        errorMessage = error.toString(),
                        isRefreshing = false
                    )
                }
            }
        )
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