package com.sinya.projects.sportsdiary.data.database.repository

import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionDialogContent
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionItem
import jakarta.inject.Inject

interface ProportionRepository {
    suspend fun proportionList() : List<Proportions>

    suspend fun getById(id: Int?): ProportionItem
    suspend fun getProportionData(id: Int): ProportionDialogContent

    suspend fun insertProportions(entity: ProportionItem)

    suspend fun delete(it: Proportions)
}


class ProportionRepositoryImpl @Inject constructor(
    private val proportionsDao: ProportionsDao
) : ProportionRepository {

    override suspend fun proportionList(): List<Proportions> {
        val list = proportionsDao.getProportionsList()
        return list
    }

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


    override suspend fun insertProportions(entity: ProportionItem) {
        proportionsDao.insertOrUpdate(entity)
    }

    override suspend fun delete(it: Proportions) {
        proportionsDao.deleteProportion(it)
    }
}
