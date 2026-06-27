package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.ProportionItem
import com.sinya.projects.sportsdiary.core.domain.repository.ProportionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveNewProportionEntityUseCase @Inject constructor(
    private val repository: ProportionRepository
) {
    operator fun invoke() : Flow<ProportionItem> {
        return repository.observeNewProportion()
    }
}