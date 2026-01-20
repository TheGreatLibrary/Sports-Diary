package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi
import jakarta.inject.Inject

class GetExerciseListUseCase  @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    suspend operator fun invoke(): List<ExerciseUi> {
        return exercisesRepository.getExercisesList()
    }
}