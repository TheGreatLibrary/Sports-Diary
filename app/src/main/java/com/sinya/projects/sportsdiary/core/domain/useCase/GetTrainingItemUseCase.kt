package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import jakarta.inject.Inject

class GetTrainingItemUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(id: Int?): Result<TrainingEntity> {
        return trainingRepo.getById(id)
    }
}