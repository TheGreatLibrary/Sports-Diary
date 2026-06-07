package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import jakarta.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    suspend operator fun invoke(it: TypeTraining): Result<Int> {
        return repoTraining.deleteCategory(it)
    }
}