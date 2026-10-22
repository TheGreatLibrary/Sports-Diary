package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.NoteItem
import com.sinya.projects.domain.repository.MorningRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    operator fun invoke() : Flow<List<NoteItem>> = repository.getNotes()
}