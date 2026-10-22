package com.sinya.projects.domain.repository

import com.sinya.projects.domain.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategoryEntity(id: Int?): Flow<CategoryEntity>
}