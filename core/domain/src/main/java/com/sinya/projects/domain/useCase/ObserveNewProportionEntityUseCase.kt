package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ProportionItem
import com.sinya.projects.domain.repository.ProportionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNewProportionEntityUseCase @Inject constructor(
    private val repository: ProportionRepository
) {
    operator fun invoke() : Flow<ProportionItem> {
        return repository.observeNewProportion()
    }
}