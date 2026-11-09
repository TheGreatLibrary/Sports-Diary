package com.sinya.projects.pagetraining.analyze

import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.model.TypeTrainingEntry
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface TrainingAnalyzeUiState {
    data object Loading : TrainingAnalyzeUiState
    data class Success(
        val id: Int,
        val date: LocalDateTime,
        val category: TypeTrainingEntry? = null,
        val summaryWeight: Float = 0f,
        val coefficient: Float = 0f,
        val countEx: Int = 0,
        val countSet: Int = 0,
        val countRep: Int = 0,
        val bodyVisible: Boolean = false,
        val dataExerciseMuscles: List<DataExerciseMuscles> = emptyList(),
        val allMuscles: List<Pair<Int, String>> = emptyList(),
    ) : TrainingAnalyzeUiState
}