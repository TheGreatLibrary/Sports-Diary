package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetMuscleDataUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(categoryId: Int?, id: Int): Result<List<DataExerciseMuscles>> {
        return repository.getMuscleDataById(id, categoryId)
    }
}
