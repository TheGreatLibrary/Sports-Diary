package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetExerciseDescriptionUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(exerciseId: Int): Result<ExerciseDialogContent> {
        return exerciseRepo.getExerciseById(exerciseId)
    }
}