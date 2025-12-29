package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.presentation.trainings.Training
import jakarta.inject.Inject

class GetTrainingListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    suspend operator fun invoke(start: String, end: String): Result<List<Training>> {
        return repoTraining.getList(start, end)
    }
}