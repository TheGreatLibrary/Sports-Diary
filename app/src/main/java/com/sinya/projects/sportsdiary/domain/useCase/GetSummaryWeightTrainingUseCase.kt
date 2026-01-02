package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject

class GetSummaryWeightTrainingUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(): Result<Float> {
        return trainingRepository.getSummaryWeightOfTrainings()
    }
}