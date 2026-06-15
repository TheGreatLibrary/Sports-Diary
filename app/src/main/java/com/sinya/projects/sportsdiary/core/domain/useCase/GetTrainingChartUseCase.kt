package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartPoint
import jakarta.inject.Inject

class GetTrainingChartUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(type: TypeTime = TypeTime.DAYS): Result<List<ChartLine>> {
        return trainingRepository.getChartList(type)
    }
}