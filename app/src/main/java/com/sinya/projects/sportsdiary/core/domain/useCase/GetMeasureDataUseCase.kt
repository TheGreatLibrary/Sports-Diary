package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.core.domain.repository.ProportionRepository
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine
import javax.inject.Inject

class GetMeasureDataUseCase @Inject constructor(
    private val repository: ProportionRepository
) {
    suspend operator fun invoke(list: List<Int>) : Result<List<ChartLine>> {
        return repository.getMeasurementData(list)
    }
}