package com.sinya.projects.database.repoImpl

import com.sinya.projects.database.dao.TypeTrainingDao
import com.sinya.projects.database.mapper.toDomain
import com.sinya.projects.domain.model.CategoryEntity
import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.domain.repository.CategoryRepository
import com.sinya.projects.domain.providers.LanguageProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
internal class CategoryRepositoryImpl @Inject constructor(
    private val typeTrainingDao: TypeTrainingDao,
    private val languageProvider: LanguageProvider
) : CategoryRepository {
    override fun observeCategoryEntity(id: Int?): Flow<CategoryEntity> {
        return languageProvider.currentLanguage.flatMapLatest { language ->
            combine(
                typeTrainingDao.observeTypeTrainingById(id),
                typeTrainingDao.observeItemsTypeTraining(id, language)
            ) { type, items ->
                CategoryEntity(
                    category = type?.toDomain() ?: TypeTrainingEntry(name = ""),
                    items = items
                )
            }
        }
    }
}