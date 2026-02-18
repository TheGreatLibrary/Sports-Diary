package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.ExerciseMusclesData
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetMusclesUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<ExerciseMusclesData>> {
        return exerciseRepo.getMuscles()
    }
}