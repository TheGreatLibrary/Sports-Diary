package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject

class GetSerialNumOfCategoryUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(typeTrainingId: Int?): Result<String> {
        return trainingRepo.getSerialNumOfCategory(typeTrainingId)
    }
}