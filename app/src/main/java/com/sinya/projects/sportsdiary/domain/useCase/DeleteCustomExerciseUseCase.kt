package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class DeleteCustomExerciseUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(id: Int): Result<Int> {
        return exerciseRepo.deleteCustomExercise(id)
    }
}