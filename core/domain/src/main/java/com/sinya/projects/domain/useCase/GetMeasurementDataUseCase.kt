package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ProportionDialogContent
import com.sinya.projects.domain.repository.ProportionRepository
import javax.inject.Inject

class GetMeasurementDataUseCase @Inject constructor(
  private val proportionRepo: ProportionRepository
) {
    suspend operator fun invoke(id: Int): Result<ProportionDialogContent> {
        return proportionRepo.getMeasurementDataById(id)
    }
}