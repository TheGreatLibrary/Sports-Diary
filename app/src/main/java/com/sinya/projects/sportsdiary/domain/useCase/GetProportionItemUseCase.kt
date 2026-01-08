package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.ProportionItem
import com.sinya.projects.sportsdiary.domain.repository.ProportionRepository
import jakarta.inject.Inject

class GetProportionItemUseCase @Inject constructor(
    private val proportionRepo: ProportionRepository
) {
    suspend operator fun invoke(id: Int?): Result<ProportionItem> {
        return proportionRepo.getById(id)
    }
}