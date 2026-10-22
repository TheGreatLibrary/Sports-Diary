package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseWithFullData
import com.sinya.projects.domain.repository.ExercisesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveExerciseDescriptionUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    operator fun invoke(exerciseId: Int): Flow<ExerciseWithFullData> {
        return exerciseRepo.observeExerciseById(exerciseId)
    }
}

