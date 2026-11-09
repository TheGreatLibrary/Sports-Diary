package com.sinya.projects.pagetraining.analyze

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.enums.TrainingMode
import com.sinya.projects.domain.enums.Visibility
import com.sinya.projects.domain.enums.WeightState
import com.sinya.projects.domain.useCase.GetMuscleDataUseCase
import com.sinya.projects.domain.useCase.GetMusclesUseCase
import com.sinya.projects.domain.useCase.GetPrevTrainingSummaryWeightUseCase
import com.sinya.projects.domain.useCase.GetWeightUserUseCase
import com.sinya.projects.domain.useCase.ObserveTrainingEntityByIdUseCase
import com.sinya.projects.domain.utils.parseStringToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.text.toFloat

@HiltViewModel
internal class TrainingAnalyzeViewModel @Inject constructor(
    private val getTrainingItemUseCase: ObserveTrainingEntityByIdUseCase,
    private val getWeightUserUseCase: GetWeightUserUseCase,
    private val getPrevTrainingSummaryWeightUseCase: GetPrevTrainingSummaryWeightUseCase,
    private val getMuscleDataUseCase: GetMuscleDataUseCase,
    private val getMusclesUseCase: GetMusclesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<TrainingAnalyzeUiState>(TrainingAnalyzeUiState.Loading)
    val state = _state.asStateFlow()


    fun loadData(id: Int) {
        _state.update {
            TrainingAnalyzeUiState.Success(
                id = id,
                date = LocalDateTime.now()
            )
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
        val initialState = _state.value as? TrainingAnalyzeUiState.Success ?: return@launch

        val allMuscles =
            getMusclesUseCase().getOrElse { emptyList() }.map { Pair(it.muscleId, it.name) }

        val weightState = getWeightUserUseCase().getOrNull() ?: 0f
        getTrainingItemUseCase(initialState.id)
            .collect { entity ->
                val dataPrev =
                    getPrevTrainingSummaryWeightUseCase(entity.category?.id, initialState.id).getOrNull()

                var weight = 0f
                entity.items.forEach { item ->
                    if (item.state == Visibility.VISIBLE) {
                        // где item — это запись подхода/упражнения из базы
                        val countArr = item.items.map { it.count }
                        val weightArr = item.items.map { it.weight }
                        val divisor = if (item.mode == TrainingMode.TIME) 4f else 1f
                        for (i in countArr.indices) {
                            val countVal = countArr.getOrNull(i)?.toFloatOrNull() ?: 0f
                            val weightVal = weightArr.getOrNull(i)?.toFloatOrNull() ?: 0f
                            weight += countVal * (weightVal + if (item.weightState == WeightState.NOT_MATTER) 0f else weightState) / divisor

                        }
                    }
                }
                weight /= 1000f


                val coefficient = when {
                    dataPrev == null || dataPrev <= 0f -> 100f
                    else -> (weight / dataPrev) * 100f
                }

                Log.d("ddd", "weight $weight dataPrev $dataPrev coefficient $coefficient")


                val listData =
                    getMuscleDataUseCase(entity.category?.id, initialState.id).getOrElse { emptyList() }

                _state.update { current ->
                    val currentSuccess = current as? TrainingAnalyzeUiState.Success ?: initialState
                    currentSuccess.copy(
                        date = parseStringToDate(entity.date),
                        allMuscles = allMuscles,
                        category = entity.category,
                        countEx = entity.items.size,
                        countSet = entity.items.sumOf { it.items.size },
                        countRep = entity.items.sumOf { items ->
                            items.items.sumOf { it.count.toFloatOrNull()?.toInt() ?: 0 }
                        },
                        summaryWeight = weight,
                        coefficient = coefficient,
                        dataExerciseMuscles = listData
                    )
                }
            }
    }
}

