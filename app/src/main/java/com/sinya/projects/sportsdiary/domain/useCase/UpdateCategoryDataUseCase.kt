package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject

class UpdateCategoryDataUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(items: List<DataTypeTrainings>): Result<Int> {
        return trainingRepo.updateDataCategory(items)
    }
}