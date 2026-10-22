package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ProportionsEntry
import com.sinya.projects.domain.repository.ProportionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProportionRangeListUseCase @Inject constructor(
    private val repoProportion: ProportionRepository
) {
    operator fun invoke(start: String, end: String): Flow<List<ProportionsEntry>> {
        return repoProportion.getList(start, end)
    }
}