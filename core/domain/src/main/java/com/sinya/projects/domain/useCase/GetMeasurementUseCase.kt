package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.DropdownCheckboxItem
import com.sinya.projects.domain.repository.ProportionRepository
import javax.inject.Inject

class GetMeasurementUseCase @Inject constructor(
    private val repository: ProportionRepository
) {
    suspend operator fun invoke(): Result<List<DropdownCheckboxItem>> {
        return repository.getMeasurementsList()
    }
}