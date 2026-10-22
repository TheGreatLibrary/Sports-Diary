package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetSerialNumOfCategoryUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(typeTrainingId: Int?): Result<String> {
        return trainingRepo.getSerialNumOfCategory(typeTrainingId)
    }
}