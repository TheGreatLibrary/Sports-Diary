package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.repository.MorningRepository
import javax.inject.Inject

class EditNoteByMorningUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke(day: MorningEntry): Result<Unit> {
        return repository.editNote(day)
    }
}