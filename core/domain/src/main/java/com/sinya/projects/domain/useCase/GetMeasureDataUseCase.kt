package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.repository.ProportionRepository
import javax.inject.Inject

class GetMeasureDataUseCase @Inject constructor(
    private val repository: ProportionRepository
) {
    suspend operator fun invoke(list: List<Int>) : Result<List<ChartLine>> {
        return repository.getMeasurementData(list)
    }
}