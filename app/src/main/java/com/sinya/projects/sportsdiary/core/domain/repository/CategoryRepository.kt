package com.sinya.projects.sportsdiary.core.domain.repository

import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.domain.model.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface CategoryRepository {
    fun observeCategoryEntity(
        id: Int?,
        locale: String = Locale.current.language
    ): Flow<CategoryEntity>

}

class CategoryRepositoryImpl @Inject constructor(
    private val typeTrainingDao: TypeTrainingDao,
) : CategoryRepository {
    override fun observeCategoryEntity(id: Int?, locale: String): Flow<CategoryEntity> {
        return combine(
            typeTrainingDao.observeTypeTrainingById(id),
            typeTrainingDao.observeItemsTypeTraining(id, locale)
        ) { type, items ->
            CategoryEntity(
                category = type ?: TypeTraining(name = ""),
                items = items
            )
        }
    }


}