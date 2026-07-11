package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.ui.features.map.model.DataExerciseMuscles
import jakarta.inject.Inject


class GetDataExerciseMusclesUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(from: String, to: String): Result<List<DataExerciseMuscles>> {
        return repository.getDataExerciseMuscles(from, to)
    }
}