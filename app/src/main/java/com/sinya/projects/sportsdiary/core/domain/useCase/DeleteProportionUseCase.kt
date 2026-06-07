package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Proportions
import com.sinya.projects.sportsdiary.core.domain.repository.ProportionRepository
import jakarta.inject.Inject

class DeleteProportionUseCase @Inject constructor(
    private val repoProportion: ProportionRepository
) {
    suspend operator fun invoke(it: Proportions): Result<Int> {
        return repoProportion.deleteProportion(it)
    }
}