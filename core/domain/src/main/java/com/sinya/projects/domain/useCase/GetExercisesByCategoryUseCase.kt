package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseItem
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetExercisesByCategoryUseCase @Inject constructor(
    private val trainingRepo: TrainingRepository
) {
    suspend operator fun invoke(id: Int): Result<List<ExerciseItem>> {
        return trainingRepo.getDataByTypeTraining(id)
    }
}