package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.ProportionItem
import com.sinya.projects.sportsdiary.core.domain.repository.ProportionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveProportionEntityUseCase @Inject constructor(
    private val proportionRepo: ProportionRepository
) {
    operator fun invoke(id: Int): Flow<ProportionItem> {
        return proportionRepo.observeProportionEntityById(id)
    }
}

