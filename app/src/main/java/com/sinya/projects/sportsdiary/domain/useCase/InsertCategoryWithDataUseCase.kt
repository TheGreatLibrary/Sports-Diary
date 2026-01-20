package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject

class InsertCategoryWithDataUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(item: TypeTraining, items: List<DataTypeTrainings>): Result<Int> {
        return trainingRepo.insertCategory(item, items)
    }
}