package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject

class CheckNameCategoryExistsUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(name: String, id: Int): Result<Boolean> {
        return trainingRepo.checkNameCategoryExists(name, id)
    }
}