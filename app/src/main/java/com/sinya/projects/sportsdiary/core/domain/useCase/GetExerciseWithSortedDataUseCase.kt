package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles
import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetExerciseWithSortedDataUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    operator fun invoke(): Flow<List<ExerciseWithMuscles>> {
        return exercisesRepository.observeExerciseTranslations()
    }
}