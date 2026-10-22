package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.MorningRepository
import javax.inject.Inject

class GetCountMorningTrainingUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke() : Result<Int> {
        return repository.getCount()
    }
}
