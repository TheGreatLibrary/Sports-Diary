package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetExercisesDataUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(list: List<Int>) : Result<List<ChartLine>> {
        return repository.getExercisesData(list)
    }
}