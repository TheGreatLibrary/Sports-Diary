package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ProportionItem
import com.sinya.projects.domain.repository.ProportionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProportionEntityUseCase @Inject constructor(
    private val proportionRepo: ProportionRepository
) {
    operator fun invoke(id: Int): Flow<ProportionItem> {
        return proportionRepo.observeProportionEntityById(id)
    }
}

