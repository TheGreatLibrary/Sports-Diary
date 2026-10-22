package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.TrainingsEntry
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class DeleteTrainingsUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    suspend operator fun invoke(items: List<TrainingsEntry>): Result<Int> {
        return repoTraining.deleteTrainings(items)
    }
}