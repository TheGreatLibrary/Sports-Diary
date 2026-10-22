package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject


class GetDataExerciseMusclesUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(from: String, to: String): Result<List<DataExerciseMuscles>> {
        return repository.getDataExerciseMuscles(from, to)
    }
}