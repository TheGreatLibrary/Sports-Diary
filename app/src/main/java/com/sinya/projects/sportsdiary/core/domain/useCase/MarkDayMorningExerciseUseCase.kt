package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataMorning
import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepository
import jakarta.inject.Inject

class MarkDayMorningExerciseUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke(morningState: Boolean, item: DataMorning): Result<Unit> {
        return repository.markDayMorning(morningState, item)
    }
}