package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.LevelTranslation
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<LevelTranslation>> {
        return exerciseRepo.getLevels()
    }
}