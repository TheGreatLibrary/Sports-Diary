package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetPrevTrainingSummaryWeightUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(categoryId: Int?, trainingId: Int): Result<Float> {
        return repository.getPrevTrainingSummaryWeight(categoryId, trainingId)
    }
}
