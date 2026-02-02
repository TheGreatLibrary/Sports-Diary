package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.ExerciseMusclesData
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetExerciseMusclesUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    suspend operator fun invoke(id: Int): Result<List<ExerciseMusclesData>> {
        return exercisesRepository.getExerciseMusclesById(id)
    }
}