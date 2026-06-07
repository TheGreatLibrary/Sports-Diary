package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.Training
import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTrainingRangeListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    operator fun invoke(start: String, end: String): Flow<List<Training>> {
        return repoTraining.getList(start, end)
    }
}