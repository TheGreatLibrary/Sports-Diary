package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Proportions
import com.sinya.projects.sportsdiary.core.domain.repository.ProportionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetProportionsUseCase @Inject constructor(
    private val repoProportion: ProportionRepository
) {
    operator fun invoke(): Flow<List<Proportions>> {
        return repoProportion.getProportionList()
    }
}