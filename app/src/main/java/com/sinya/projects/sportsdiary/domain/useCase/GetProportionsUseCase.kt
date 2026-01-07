package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.domain.repository.ProportionRepository
import jakarta.inject.Inject

class GetProportionsUseCase @Inject constructor(
    private val repoProportion: ProportionRepository
) {
    suspend operator fun invoke(): Result<List<Proportions>> {
        return repoProportion.getProportionList()
    }
}