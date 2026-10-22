package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.DataTypeTrainingsEntry
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class UpdateCategoryDataUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(items: List<DataTypeTrainingsEntry>): Result<Int> {
        return trainingRepo.updateDataCategory(items)
    }
}