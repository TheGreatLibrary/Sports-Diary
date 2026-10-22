package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseDialogContent
import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class GetExerciseDescriptionUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(exerciseId: Int): Result<ExerciseDialogContent> {
        return exerciseRepo.getExerciseById(exerciseId)
    }
}