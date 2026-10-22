package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetCountTrainingUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return trainingRepository.getCountOfTrainings()
    }
}