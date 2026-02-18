package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.ForceTranslation
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetForcesUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<ForceTranslation>> {
        return exerciseRepo.getForces()
    }
}