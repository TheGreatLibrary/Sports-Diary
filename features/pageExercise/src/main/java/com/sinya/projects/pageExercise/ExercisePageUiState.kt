package com.sinya.projects.pageExercise

import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.model.ExerciseMusclesData
import com.sinya.projects.domain.model.ExerciseWithFullData

sealed interface ExercisePageUiState {
    data object Loading : ExercisePageUiState

    data class Success(
        val exercise: ExerciseWithFullData,
        val exMuscles: List<ExerciseMusclesData> = emptyList(),
        val bodyVisible: Boolean = false,
        val rules: List<String>,                 // Уже порезанные и очищенные правила
        val listMuscle: List<DataExerciseMuscles>, // Уже смапленные мышцы для BodyCard
        val mediaItems: List<ExerciseMediaItem>,
        val allMuscles: List<Pair<Int, String>>,
        val nameExerciseForImages: String
    ) : ExercisePageUiState

    data class Error(val message: String) : ExercisePageUiState
}

sealed interface ExerciseMediaItem {
    data class CustomPhoto(val uri: String) : ExerciseMediaItem
    data class Gif(val url: String) : ExerciseMediaItem
    data class PhotoSlideshow(val firstUrl: String, val secondUrl: String) : ExerciseMediaItem
}