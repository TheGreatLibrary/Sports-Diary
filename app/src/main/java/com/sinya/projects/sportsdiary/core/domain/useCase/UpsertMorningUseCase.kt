package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataMorning
import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepository
import jakarta.inject.Inject

class UpsertMorningUseCase @Inject constructor(
    private val repository: MorningRepository
){
    suspend operator fun invoke(item: DataMorning) : Result<Int> {
        return repository.insertMorning(item)
    }
}