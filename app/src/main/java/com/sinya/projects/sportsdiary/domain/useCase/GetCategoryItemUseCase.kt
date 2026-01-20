package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.CategoryEntity
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject

class GetCategoryItemUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(id: Int?): Result<CategoryEntity> {
        return trainingRepo.getCategoryEntity(id)
    }
}