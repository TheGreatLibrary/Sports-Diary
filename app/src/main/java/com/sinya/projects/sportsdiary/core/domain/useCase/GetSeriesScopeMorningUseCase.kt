package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepository
import jakarta.inject.Inject

class GetSeriesScopeMorningUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return repository.getSeriesScopeMorning()
    }
}
