package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.MorningDay
import com.sinya.projects.sportsdiary.domain.repository.MorningRepository
import jakarta.inject.Inject

class GetMorningListUseCase @Inject constructor(
    private val morningRepo: MorningRepository
) {
    suspend operator fun invoke(start: String, end: String): Result<List<MorningDay>> {
        return morningRepo.getList(start, end)
    }
}