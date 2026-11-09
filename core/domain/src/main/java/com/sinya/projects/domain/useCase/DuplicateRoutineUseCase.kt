package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.CategoryRepository
import javax.inject.Inject

class DuplicateRoutineUseCase @Inject constructor(
    private val routineRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Int): Result<Int> {
        return routineRepository.duplicateRoutine(id)
    }
}