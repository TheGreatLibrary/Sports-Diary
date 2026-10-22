package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.repository.MorningRepository
import javax.inject.Inject

class MarkDaysListMorningUseCase @Inject constructor(
    private val repo: MorningRepository
) {
    suspend operator fun invoke(items: List<MorningEntry>, marked: Boolean): Result<Unit> {
        return repo.markDaysListMorning(marked, items)
    }
}
