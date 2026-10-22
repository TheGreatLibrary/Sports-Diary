package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.CategoryEntity
import com.sinya.projects.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCategoryEntityUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(id: Int?): Flow<CategoryEntity> {
        return repository.observeCategoryEntity(id)
    }
}