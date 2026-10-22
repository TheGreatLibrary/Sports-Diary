package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.MorningRepository
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke(list: List<Int>) : Result<Unit> {
        return repository.deleteNotes(list)
    }
}