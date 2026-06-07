package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import jakarta.inject.Inject

class GetCountTrainingUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return trainingRepository.getCountOfTrainings()
    }
}