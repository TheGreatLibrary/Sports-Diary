package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class GetExerciseWithSortedDataUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<ExerciseWithMuscles>> {
        return exercisesRepository.getExercisesWithTranslations()
    }
}