package com.sinya.projects.pagecategory.analyze

import com.sinya.projects.domain.model.ExerciseMusclesData

internal sealed interface RoutinePageAnalyzeUiState {
    data object Loading : RoutinePageAnalyzeUiState

    data class Success(
        val id: Int,
        val dataExerciseMuscles:List<ExerciseMusclesData> = emptyList(),
        val allMuscles: List<Pair<Int, String>> = emptyList(),
        val bodyVisible: Boolean = false
    ) : RoutinePageAnalyzeUiState {

    }

}