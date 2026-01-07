package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.Training
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject

class GetTrainingListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    suspend operator fun invoke(): Result<List<Training>> {
        return repoTraining.getTrainingList()
    }
}