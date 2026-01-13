package com.sinya.projects.sportsdiary.domain.repository

import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.domain.model.ProportionDialogContent
import com.sinya.projects.sportsdiary.domain.model.ProportionItem
import jakarta.inject.Inject
import java.time.LocalDate

interface ProportionRepository {
    // Proportions
    suspend fun getProportionList(): Result<List<Proportions>>
    suspend fun deleteProportion(it: Proportions): Result<Int>

    // ProportionPage
    suspend fun getById(id: Int?): Result<ProportionItem>
    suspend fun getMeasurementDataById(id: Int): Result<ProportionDialogContent>
    suspend fun insertOrUpdateProportion(entity: ProportionItem): Result<Int>
}

class ProportionRepositoryImpl @Inject constructor(
    private val proportionsDao: ProportionsDao
) : ProportionRepository {

    // Proportions
    override suspend fun getProportionList(): Result<List<Proportions>> {
        return try {
            val list = proportionsDao.getProportionsList()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProportion(it: Proportions): Result<Int> {
        return try {
            val result = proportionsDao.deleteProportion(it)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ProportionPage
    override suspend fun getById(id: Int?): Result<ProportionItem> {
        return try {
            val locale = Locale.current.language

            if (id == null) {
                val items = proportionsDao.newProportionsWithPrevData(locale)

                Result.success(
                    ProportionItem(
                        id = null,
                        title = "",
                        date = LocalDate.now().toString(),
                        items = items
                    )
                )
            }
            else {
                val page = proportionsDao.getById(id) ?:
                    return Result.failure(IllegalStateException("Proportion with id=$id not found"))

                val items = proportionsDao.proportionPage(id, locale)

                Result.success(
                    ProportionItem(
                        id = page.id,
                        title = page.id.toString(),
                        date = page.date,
                        items = items
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMeasurementDataById(id: Int): Result<ProportionDialogContent> {
        return try {
            val result = proportionsDao.getMeasurementDataById(id, Locale.current.language)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertOrUpdateProportion(entity: ProportionItem): Result<Int> {
        return try {
            val result = proportionsDao.insertOrUpdate(entity)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
