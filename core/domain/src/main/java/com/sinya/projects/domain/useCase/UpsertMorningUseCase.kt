package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.repository.MorningRepository
import javax.inject.Inject

class UpsertMorningUseCase @Inject constructor(
    private val repository: MorningRepository
){
    suspend operator fun invoke(item: MorningEntry) : Result<Int> {
        return repository.insertMorning(item)
    }
}