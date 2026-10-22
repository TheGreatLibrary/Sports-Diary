package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ProportionItem
import com.sinya.projects.domain.repository.ProportionRepository
import javax.inject.Inject

class UpsertProportionUseCase @Inject constructor(
    private val proportionRepo: ProportionRepository
) {
    suspend operator fun invoke(item: ProportionItem): Result<Int> {
        return proportionRepo.insertOrUpdateProportion(item)
    }
}