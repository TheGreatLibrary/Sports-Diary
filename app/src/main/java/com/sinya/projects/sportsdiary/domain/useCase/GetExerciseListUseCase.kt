package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.ExerciseUi
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetExerciseListUseCase  @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<ExerciseUi>> {
        return exercisesRepository.getExercisesList()
    }
}