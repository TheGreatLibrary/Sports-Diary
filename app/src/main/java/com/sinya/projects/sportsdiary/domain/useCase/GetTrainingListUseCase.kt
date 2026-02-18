package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.Training
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTrainingListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    operator fun invoke(): Flow<List<Training>> {
        return repoTraining.getTrainingList()
    }
}