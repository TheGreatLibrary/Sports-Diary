package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetExerciseWithSortedDataUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<ExerciseWithMuscles>> {
        return exercisesRepository.getExercisesWithTranslations()
    }
}