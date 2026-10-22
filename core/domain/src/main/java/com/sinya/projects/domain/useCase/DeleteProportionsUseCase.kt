package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ProportionsEntry
import com.sinya.projects.domain.repository.ProportionRepository
import javax.inject.Inject

class DeleteProportionsUseCase @Inject constructor(
    private val repoProportion: ProportionRepository
) {
    suspend operator fun invoke(it: List<ProportionsEntry>): Result<Int> {
        return repoProportion.deleteProportion(it)
    }
}