package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.ui.features.diagram.ChartPoint
import jakarta.inject.Inject

class GetTrainingChartUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(type: TypeTime = TypeTime.DAYS): Result<List<ChartPoint>> {
        return trainingRepository.getChartList(type)
    }
}