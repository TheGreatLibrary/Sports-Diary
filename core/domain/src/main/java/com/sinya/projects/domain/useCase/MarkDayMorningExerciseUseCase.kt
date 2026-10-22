package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.repository.MorningRepository
import javax.inject.Inject

class MarkDayMorningExerciseUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke(morningState: Boolean, item: MorningEntry): Result<Unit> {
        return repository.markDayMorning(morningState, item)
    }
}