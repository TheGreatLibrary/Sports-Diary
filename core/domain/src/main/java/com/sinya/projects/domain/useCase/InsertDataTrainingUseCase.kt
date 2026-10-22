package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.DataTrainingEntry
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class InsertDataTrainingUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(items: List<DataTrainingEntry>): Result<Int> {
        return trainingRepo.insertDataTraining(items)
    }
}