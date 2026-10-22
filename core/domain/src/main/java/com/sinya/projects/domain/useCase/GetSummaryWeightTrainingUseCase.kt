package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetSummaryWeightTrainingUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(): Result<Float> {
        return trainingRepository.getSummaryWeightOfTrainings()
    }
}