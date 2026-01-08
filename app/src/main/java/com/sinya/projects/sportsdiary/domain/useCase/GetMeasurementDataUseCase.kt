package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.ProportionDialogContent
import com.sinya.projects.sportsdiary.domain.repository.ProportionRepository
import jakarta.inject.Inject

class GetMeasurementDataUseCase @Inject constructor(
  private val proportionRepo: ProportionRepository
) {
    suspend operator fun invoke(id: Int): Result<ProportionDialogContent> {
        return proportionRepo.getMeasurementDataById(id)
    }
}