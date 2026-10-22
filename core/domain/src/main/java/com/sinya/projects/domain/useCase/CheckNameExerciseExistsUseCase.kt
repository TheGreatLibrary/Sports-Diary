package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class CheckNameExerciseExistsUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(name: String, id: Int): Result<Boolean> {
        return exerciseRepo.checkNameExerciseExists(name, id)
    }
}