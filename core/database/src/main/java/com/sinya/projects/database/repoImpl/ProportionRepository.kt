package com.sinya.projects.database.repoImpl

import androidx.core.graphics.toColor
import com.sinya.projects.database.dao.ProportionsDao
import com.sinya.projects.database.mapper.toDomain
import com.sinya.projects.database.mapper.toEntity
import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.model.DropdownCheckboxItem
import com.sinya.projects.domain.model.ProportionDialogContent
import com.sinya.projects.domain.model.ProportionItem
import com.sinya.projects.domain.model.ProportionsEntry
import com.sinya.projects.domain.providers.LanguageProvider
import com.sinya.projects.domain.repository.ProportionRepository
import com.sinya.projects.domain.utils.getSortedList
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
internal class ProportionRepositoryImpl @Inject constructor(
    private val proportionsDao: ProportionsDao,
    private val languageProvider: LanguageProvider
) : ProportionRepository {

    // Proportions
    override fun getProportionList(): Flow<List<ProportionsEntry>> {
        return proportionsDao.getProportionsList()
            .map { item ->
                item.map {
                    it.toDomain()
                }
            }.catch {
                emit(emptyList())
            }
    }

    override fun getList(start: String, end: String): Flow<List<ProportionsEntry>> {
        return proportionsDao.getList(start, end).catch { emit(emptyList()) }
    }

    override suspend fun deleteProportion(it: List<ProportionsEntry>): Result<Int> {
        return runCatching {proportionsDao.deleteProportion(it.map { it.toEntity() }) }
    }

    // ProportionPage
    override fun observeProportionEntityById(id: Int): Flow<ProportionItem> {
        return languageProvider.currentLanguage.flatMapLatest { language ->
            combine(
                proportionsDao.getById(id),
                proportionsDao.proportionPage(id, language)
            ) { page, items ->
                when (page) {
                    null -> throw IllegalStateException("Proportion with id=$id not found")
                    else -> ProportionItem(page.id, page.id.toString(), page.date, items)
                }
            }
        }
    }

    override fun observeNewProportion(): Flow<ProportionItem> {
        return languageProvider.currentLanguage.flatMapLatest { language ->
            proportionsDao.newProportionsWithPrevData(language).map { items ->
                ProportionItem(null, "", LocalDate.now().toString(), items)
            }
        }
    }

    override suspend fun getMeasurementDataById(id: Int): Result<ProportionDialogContent> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            proportionsDao.getMeasurementDataById(id, lang)
        }
    }

    override suspend fun insertOrUpdateProportion(entity: ProportionItem): Result<Int> {
        return runCatching { proportionsDao.insertOrUpdate(entity) }
    }

    override suspend fun getMeasurementsList(): Result<List<DropdownCheckboxItem>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            proportionsDao.getMeasurementsTitles(lang).getSortedList()
        }
    }

    override suspend fun getMeasurementData(list: List<Int>): Result<List<ChartLine>> {
        return runCatching {
            proportionsDao.getMeasurementDataList(list).sortedBy { it.date }.groupBy { it.id }
                .map {
                    ChartLine(
                        id = it.key,
                        points = it.value,
                        lineColor = 0xFFFFFFF.toColor()
                    )
                }
        }
    }
}
