package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.TrainingEntity
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetNewTrainingUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    operator fun invoke(date: String?) : Result<TrainingEntity> {
        return repository.getNewTraining(date)
    }
}
