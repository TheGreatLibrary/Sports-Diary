package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.Training
import com.sinya.projects.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrainingListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    operator fun invoke(): Flow<List<Training>> {
        return repoTraining.getTrainingList()
    }
}