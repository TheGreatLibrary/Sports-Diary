package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.repository.ProportionRepository
import com.sinya.projects.sportsdiary.ui.features.DropdownCheckboxItem
import javax.inject.Inject

class GetMeasurementUseCase @Inject constructor(
    private val repository: ProportionRepository
) {
    suspend operator fun invoke(): Result<List<DropdownCheckboxItem>> {
        return repository.getMeasurementsList()
    }
}