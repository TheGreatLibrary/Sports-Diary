package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.DataTypeTrainingsEntry
import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class InsertCategoryWithDataUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(item: TypeTrainingEntry, items: List<DataTypeTrainingsEntry>): Result<Int> {
        return trainingRepo.insertCategory(item, items)
    }
}