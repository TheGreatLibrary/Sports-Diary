package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.domain.repository.ProportionRepository
import jakarta.inject.Inject

class DeleteProportionUseCase @Inject constructor(
    private val repoProportion: ProportionRepository
) {
    suspend operator fun invoke(it: Proportions): Result<Int> {
        return repoProportion.deleteProportion(it)
    }
}