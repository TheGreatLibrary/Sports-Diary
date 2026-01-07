package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.domain.model.Training
import jakarta.inject.Inject

class GetTrainingRangeListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    suspend operator fun invoke(start: String, end: String): Result<List<Training>> {
        return repoTraining.getList(start, end)
    }
}