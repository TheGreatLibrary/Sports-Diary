package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class DeleteCustomExerciseUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(id: Int): Result<Int> {
        return exerciseRepo.deleteCustomExercise(id)
    }
}