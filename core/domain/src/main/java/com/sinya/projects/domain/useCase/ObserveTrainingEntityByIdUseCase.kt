package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.TrainingEntity
import com.sinya.projects.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTrainingEntityByIdUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    operator fun invoke(id: Int): Flow<TrainingEntity> {
        return trainingRepo.observeTrainingEntityById(id)
    }
}