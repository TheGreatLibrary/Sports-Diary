package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.TrainingEntity
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class UpsertTrainingUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(item: TrainingEntity): Result<Int> {
        return trainingRepo.upsertTraining(item)
    }
}