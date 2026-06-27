package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTrainingEntityByIdUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    operator fun invoke(id: Int): Flow<TrainingEntity> {
        return trainingRepo.observeTrainingEntityById(id)
    }
}