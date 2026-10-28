package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class GetExerciseNameUseCase @Inject constructor(
    private val repository: ExercisesRepository
) {
    suspend operator fun invoke(id: Int): Result<String> {
        return repository.getEnglishExerciseName(id)
    }
}