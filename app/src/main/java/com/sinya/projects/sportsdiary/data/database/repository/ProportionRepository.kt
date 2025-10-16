package com.sinya.projects.sportsdiary.data.database.repository

import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionItem
import com.sinya.projects.sportsdiary.presentation.proportions.Proportion
import jakarta.inject.Inject


interface ProportionRepository {
    suspend fun proportionList() : List<Proportion>

    suspend fun getById(id: Int): ProportionItem
    suspend fun getNewListProportions(): ProportionItem

    suspend fun insertProportions(entity: ProportionItem)
    suspend fun updateProportions(entity: ProportionItem)

    suspend fun delete(id: Int)
}

class ProportionRepositoryImpl @Inject constructor(
    private val proportionsDao: ProportionsDao
) : ProportionRepository {

    override suspend fun proportionList(): List<Proportion> {
        val list = proportionsDao.getProportionsList()
        return list
    }

    override suspend fun getNewListProportions() : ProportionItem {
        val page = proportionsDao.getById(null)?: Proportions()
        val list = proportionsDao.newProportions()
        return ProportionItem(
            id = page.id,
            title = page.id.toString(),
            date = page.date,
            items = list
        )
    }

    override suspend fun getById(id: Int): ProportionItem {
        val page = proportionsDao.getById(id) ?: Proportions()
        val list = proportionsDao.proportionPage(id)
        return ProportionItem(
            id = page.id,
            title = page.id.toString(),
            date = page.date,
            items = list
        )
    }


    override suspend fun insertProportions(entity: ProportionItem) {
        proportionsDao.insertOrUpdate(entity)
    }

    override suspend fun updateProportions(entity: ProportionItem) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}
