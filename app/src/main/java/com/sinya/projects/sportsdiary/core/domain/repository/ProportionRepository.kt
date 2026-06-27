package com.sinya.projects.sportsdiary.core.domain.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.ProportionsDao
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Proportions
import com.sinya.projects.sportsdiary.core.domain.model.ProportionDialogContent
import com.sinya.projects.sportsdiary.core.domain.model.ProportionItem
import com.sinya.projects.sportsdiary.core.utils.getSortedList
import com.sinya.projects.sportsdiary.ui.features.DropdownCheckboxItem
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

interface ProportionRepository {
    // Proportions
    fun getProportionList(): Flow<List<Proportions>>
    suspend fun deleteProportion(it: Proportions): Result<Int>

    // ProportionPage

    suspend fun getMeasurementDataById(id: Int): Result<ProportionDialogContent>
    suspend fun insertOrUpdateProportion(entity: ProportionItem): Result<Int>
    suspend fun getMeasurementsList(): Result<List<DropdownCheckboxItem>>
    suspend fun getMeasurementData(list: List<Int>): Result<List<ChartLine>>

    fun observeProportionEntityById(id: Int, locale: String = Locale.current.language): Flow<ProportionItem>
    fun observeNewProportion(locale: String = Locale.current.language): Flow<ProportionItem>
}

class ProportionRepositoryImpl @Inject constructor(
    private val proportionsDao: ProportionsDao
) : ProportionRepository {

    // Proportions
    override fun getProportionList(): Flow<List<Proportions>> {
        return proportionsDao.getProportionsList().catch {
            emit(emptyList())
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
    override fun observeProportionEntityById(id: Int, locale: String): Flow<ProportionItem> {
        return combine(
            proportionsDao.getById(id),
            proportionsDao.proportionPage(id, locale)
        ) { page, items ->
            when (page) {
                null -> throw IllegalStateException("Proportion with id=$id not found")
                else -> ProportionItem(page.id, page.id.toString(), page.date, items)
            }
        }
    }

    override fun observeNewProportion(locale: String): Flow<ProportionItem> {
        return proportionsDao.newProportionsWithPrevData(locale).map { items ->
            ProportionItem(null, "", LocalDate.now().toString(), items)
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

    override suspend fun getMeasurementsList(): Result<List<DropdownCheckboxItem>> {
        return try {
            val result =
                proportionsDao.getMeasurementsTitles(Locale.current.language).getSortedList()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMeasurementData(
        list: List<Int>,
    ): Result<List<ChartLine>> {
        return try {
            val raw =
                proportionsDao.getMeasurementDataList(list).sortedBy { it.date }.groupBy { it.id }
                    .map {
                        ChartLine(
                            id = it.key,
                            points = it.value,
                            lineColor = Color.Unspecified
                        )
                    }

            Result.success(raw)
        } catch (e: Exception) {
            Result.failure(e)

        }
    }
}
