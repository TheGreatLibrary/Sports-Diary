package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.ProportionItem
import com.sinya.projects.sportsdiary.domain.repository.ProportionRepository
import jakarta.inject.Inject

class UpsertProportionUseCase @Inject constructor(
    private val proportionRepo: ProportionRepository
) {
    suspend operator fun invoke(item: ProportionItem): Result<Int> {
        return proportionRepo.insertOrUpdateProportion(item)
    }
}