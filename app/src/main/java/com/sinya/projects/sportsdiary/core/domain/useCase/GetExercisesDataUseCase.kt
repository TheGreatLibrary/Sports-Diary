package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine
import javax.inject.Inject

class GetExercisesDataUseCase @Inject constructor(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(list: List<Int>) : Result<List<ChartLine>> {
        return repository.getExercisesData(list)
    }
}