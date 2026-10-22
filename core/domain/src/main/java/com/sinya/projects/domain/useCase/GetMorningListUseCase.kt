package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.MorningDay
import com.sinya.projects.domain.repository.MorningRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMorningListUseCase @Inject constructor(
    private val morningRepo: MorningRepository
) {
    operator fun invoke(start: String, end: String): Flow<List<MorningDay>> {
        return morningRepo.getList(start, end)
    }
}