package com.sinya.projects.pagetraining.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.useCase.GetMuscleDataUseCase
import com.sinya.projects.domain.useCase.GetMusclesUseCase
import com.sinya.projects.domain.useCase.GetPrevTrainingSummaryWeightUseCase
import com.sinya.projects.domain.useCase.ObserveTrainingEntityByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TrainingAnalyzeViewModel @Inject constructor(
    private val getTrainingItemUseCase: ObserveTrainingEntityByIdUseCase,
    private val getPrevTrainingSummaryWeightUseCase: GetPrevTrainingSummaryWeightUseCase,
    private val getMuscleDataUseCase: GetMuscleDataUseCase,
    private val getMusclesUseCase: GetMusclesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<TrainingAnalyzeUiState>(TrainingAnalyzeUiState.Loading)
    val state = _state.asStateFlow()


    fun loadData(id: Int) {
        _state.update {
            TrainingAnalyzeUiState.Success(id = id)
        }

        observeTrainingEntity()
    }

    fun onEvent(event: TrainingAnalyzeEvent) {
        when (event) {
            is TrainingAnalyzeEvent.OnBodyInfoClick -> {
                _state.update {
                    (it as TrainingAnalyzeUiState.Success).copy(
                        bodyVisible = !it.bodyVisible
                    )
                }
            }
        }
    }

    private fun observeTrainingEntity() = viewModelScope.launch {
        val state = _state.value as? TrainingAnalyzeUiState.Success ?: return@launch

        val allMuscles =
            getMusclesUseCase().getOrElse { emptyList() }.map { Pair(it.muscleId, it.name) }

        getTrainingItemUseCase(state.id)
            .collect { entity ->
                val weight = entity.items.sumOf { category ->
                    category.items.sumOf { it.count.toInt() * it.weight.toInt() }
                }.toFloat() / 1000

                val weightPrev = getPrevTrainingSummaryWeightUseCase(entity.category?.id, state.id)
                    .getOrNull()

                val coefficient = when {
                    weightPrev == null || weightPrev <= 0f -> 100f
                    else -> (weight / weightPrev) * 100f
                }

                val listData = getMuscleDataUseCase(entity.category?.id, state.id).getOrElse { emptyList() }
                _state.update {
                    state.copy(
                        allMuscles = allMuscles,
                        category = entity.category,
                        countEx = entity.items.size,
                        countSet = entity.items.sumOf { it.items.size },
                        countRep = entity.items.sumOf { items -> items.items.sumOf { it.count.toInt() } },
                        summaryWeight = weight,
                        coefficient = coefficient,
                        dataExerciseMuscles = listData
                    )
                }
            }
    }


}

