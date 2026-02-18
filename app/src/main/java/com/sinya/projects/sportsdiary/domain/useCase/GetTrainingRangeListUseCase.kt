package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.domain.model.Training
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTrainingRangeListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    operator fun invoke(start: String, end: String): Flow<List<Training>> {
        return repoTraining.getList(start, end)
    }
}