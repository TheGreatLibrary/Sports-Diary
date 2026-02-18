package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.ExerciseWithFullData
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetExerciseDescriptionUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    operator fun invoke(exerciseId: Int): Flow<ExerciseWithFullData> {
        return exerciseRepo.getExerciseById(exerciseId)
    }
}