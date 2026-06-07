package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class CheckNameExerciseExistsUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(name: String, id: Int): Result<Boolean> {
        return exerciseRepo.checkNameExerciseExists(name, id)
    }
}