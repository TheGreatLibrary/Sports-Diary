package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class DeleteRoutinesUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    suspend operator fun invoke(it: List<TypeTrainingEntry>): Result<Int> {
        return repoTraining.deleteCategory(it)
    }
}