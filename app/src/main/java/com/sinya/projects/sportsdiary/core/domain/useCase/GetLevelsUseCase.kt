package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.LevelTranslation
import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<LevelTranslation>> {
        return exerciseRepo.getLevels()
    }
}