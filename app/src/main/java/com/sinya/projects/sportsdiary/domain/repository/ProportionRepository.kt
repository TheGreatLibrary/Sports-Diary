package com.sinya.projects.sportsdiary.domain.repository

import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionDialogContent
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionItem
import jakarta.inject.Inject

interface ProportionRepository {
    // Proportions
    suspend fun getProportionList() : Result<List<Proportions>>
    suspend fun deleteProportion(it: Proportions) : Result<Int>

    suspend fun insertProportion(entity: ProportionItem)

    // ProportionPage
    suspend fun getById(id: Int?): ProportionItem
    suspend fun getProportionData(id: Int): ProportionDialogContent
}

class ProportionRepositoryImpl @Inject constructor(
    private val proportionsDao: ProportionsDao
) : ProportionRepository {

    // Proportions
    override suspend fun getProportionList(): Result<List<Proportions>> {
        return try {
            val list = proportionsDao.getProportionsList()
            Result.success(list)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProportion(it: Proportions): Result<Int> {
        return try {
            val result =  proportionsDao.deleteProportion(it)
            Result.success(result)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ProportionPage
    override suspend fun getById(id: Int?): ProportionItem {
        val page = proportionsDao.getById(id)
        val locale = Locale.current.language
        val list = if (id == null) proportionsDao.newProportionsWithPrevData(locale) else proportionsDao.proportionPage(id, locale)
        return ProportionItem(
            id = page.id,
            title = page.id.toString(),
            date = page.date,
            items = list
        )
    }

    override suspend fun getProportionData(id: Int): ProportionDialogContent {
        return proportionsDao.getProportionById(id, Locale.current.language)
    }


    override suspend fun insertProportion(entity: ProportionItem) {
        proportionsDao.insertOrUpdate(entity)
    }

}
