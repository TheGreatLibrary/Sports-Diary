package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class CheckNameCategoryExistsUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(name: String, id: Int): Result<Boolean> {
        return trainingRepo.checkNameCategoryExists(name, id)
    }
}