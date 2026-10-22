package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.repository.ExercisesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveExerciseWithSortedDataUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    operator fun invoke(): Flow<List<ExerciseWithMuscles>> {
        return exercisesRepository.observeExerciseTranslations()
    }
}

