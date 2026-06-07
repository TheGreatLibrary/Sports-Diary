package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.MorningDay
import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetMorningListUseCase @Inject constructor(
    private val morningRepo: MorningRepository
) {
    operator fun invoke(start: String, end: String): Flow<List<MorningDay>> {
        return morningRepo.getList(start, end)
    }
}