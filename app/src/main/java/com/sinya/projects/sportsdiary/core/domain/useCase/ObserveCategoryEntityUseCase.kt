package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.CategoryEntity
import com.sinya.projects.sportsdiary.core.domain.repository.CategoryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCategoryEntityUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(id: Int?): Flow<CategoryEntity> {
        return repository.observeCategoryEntity(id)
    }
}