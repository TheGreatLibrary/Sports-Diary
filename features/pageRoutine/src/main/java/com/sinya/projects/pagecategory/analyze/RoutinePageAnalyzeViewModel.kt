package com.sinya.projects.pagecategory.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.useCase.GetExerciseMusclesByIdCategory
import com.sinya.projects.domain.useCase.GetMusclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RoutinePageAnalyzeViewModel @Inject constructor(
    private val getExerciseMusclesByIdUseCase: GetExerciseMusclesByIdCategory,
    private val getMusclesUseCase: GetMusclesUseCase,
) : ViewModel() {
    private val _state =
        MutableStateFlow<RoutinePageAnalyzeUiState>(RoutinePageAnalyzeUiState.Loading)
    val state = _state.asStateFlow()

    fun loadData(id: Int) {
        _state.update {
            RoutinePageAnalyzeUiState.Success(id)
        }

        observeMapData()
    }

    fun onEvent(event: RoutinePageAnalyzeEvent) {
        when (event) {
            is RoutinePageAnalyzeEvent.OnBodyInfoClick -> {
                _state.update {
                    (it as RoutinePageAnalyzeUiState.Success).copy(
                        bodyVisible = !it.bodyVisible
                    )
                }
            }
        }
    }

    private fun observeMapData() = viewModelScope.launch {
        val state = _state.value as? RoutinePageAnalyzeUiState.Success ?: return@launch

        val allMuscles =
            getMusclesUseCase().getOrElse { emptyList() }.map { Pair(it.muscleId, it.name) }

        getExerciseMusclesByIdUseCase(state.id).collect { list ->
            _state.update { state.copy(dataExerciseMuscles = list, allMuscles = allMuscles) }
        }
    }
}