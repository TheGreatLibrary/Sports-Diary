package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.ProportionRepository
import javax.inject.Inject

class GetWeightUserUseCase @Inject constructor(
    private val repository: ProportionRepository
) {
    suspend operator fun invoke(): Result<Float> {
        return repository.getWeightUser()
    }
}