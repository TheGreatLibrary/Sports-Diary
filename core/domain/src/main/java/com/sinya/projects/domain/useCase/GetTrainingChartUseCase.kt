package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.enums.TypeTime
import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.repository.TrainingRepository
import javax.inject.Inject

class GetTrainingChartUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(type: TypeTime = TypeTime.DAYS): Result<List<ChartLine>> {
        return trainingRepository.getChartList(type)
    }
}