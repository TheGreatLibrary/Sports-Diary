package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import javax.inject.Inject

class GetNewTrainingUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    operator fun invoke(date: String?) : Result<TrainingEntity> {
        return repository.getNewTraining(date)
    }
}
