package com.sinya.projects.database.repoImpl

import android.util.Log
import com.sinya.projects.database.dao.DataMorningDao
import com.sinya.projects.database.entity.DataMorning
import com.sinya.projects.database.mapper.toDomain
import com.sinya.projects.database.mapper.toEntity
import com.sinya.projects.domain.model.MorningDay
import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.model.NoteItem
import com.sinya.projects.domain.repository.MorningRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.time.LocalDate

internal class MorningRepositoryImpl @Inject constructor(
    private val dataMorningDao: DataMorningDao,
) : MorningRepository {
    override fun getList(start: String, end: String): Flow<List<MorningDay>> {
        return dataMorningDao.getDataOfMonth(start, end).catch { emit(emptyList()) }
    }

    override suspend fun markDaysListMorning(
        marked: Boolean,
        items: List<MorningEntry>
    ): Result<Unit> {
        return runCatching {
            val dates = items.map { it.date }
            val existingItems = dataMorningDao.getByDates(dates).associateBy { it.date }

            if (marked) {
                val toInsert = mutableListOf<DataMorning>()
                val toUpdate = mutableListOf<DataMorning>()

                items.forEach { item ->
                    val existing = existingItems[item.date]

                    if (existing != null) {
                        toUpdate.add(existing.copy(note = item.note))
                    } else {
                        toInsert.add(item.toEntity())
                    }
                }

                if (toInsert.isNotEmpty()) dataMorningDao.insertMorningList(toInsert)
                if (toUpdate.isNotEmpty()) dataMorningDao.updateMorningList(toUpdate)

            } else {
                val itemsToDelete = existingItems.values.toList()

                if (itemsToDelete.isNotEmpty()) {
                    dataMorningDao.deleteMorningList(itemsToDelete)
                }
            }
        }
    }

    override suspend fun deleteNotes(list: List<Int>): Result<Unit> {
        return runCatching { dataMorningDao.deleteNotes(list) }
    }

    override suspend fun editNote(day: MorningEntry): Result<Unit> {
        return runCatching { dataMorningDao.editNote(day.id, day.note) }
    }

    override fun getNotes(): Flow<List<NoteItem>> {
        return dataMorningDao.getNotes().catch { emit(emptyList()) }
    }

    override suspend fun getCount(): Result<Int> {
        return runCatching { dataMorningDao.getCount() }
    }

    override suspend fun getSeriesScopeMorning(): Result<Int> {
        return runCatching {
            val items = dataMorningDao.getAllMornings()
            calculateStreak(items)
        }
    }

    private fun calculateStreak(mornings: List<MorningDay>): Int {
        if (mornings.isEmpty()) return 0

        var streak = 0
        val today = LocalDate.now()
        var checkDate = today

        for (morning in mornings) {
            val morningDate = LocalDate.parse(morning.date)

            if (morningDate == checkDate) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else if (morningDate < checkDate) {
                break
            }
        }

        return streak
    }

    override suspend fun insertMorning(item: MorningEntry): Result<Int> {
        return runCatching {
            val itemInDB = dataMorningDao.getByDate(item.date)
            if (itemInDB != null) dataMorningDao.updateMorningExercise(itemInDB.copy(note = item.note))
            else dataMorningDao.insertMorningComplete(item.toEntity()).toInt()
        }
    }

    override suspend fun deleteMorning(item: MorningEntry): Result<Int> {
        return runCatching {
            val itemInDB = dataMorningDao.getByDate(item.date)
            if (itemInDB != null) {
                dataMorningDao.deleteMorning(item.toEntity())
            }
            itemInDB?.id ?: 1
        }
    }

    override suspend fun getMorningByDate(date: String): Result<MorningEntry?> {
        return runCatching { dataMorningDao.getByDate(date)?.toDomain() }
    }

    override suspend fun markDayMorning(morningState: Boolean, item: MorningEntry): Result<Unit> {
        return runCatching {
            val newItem = dataMorningDao.getByDate(item.date)

            if (morningState) {
                if (newItem != null) {
                    dataMorningDao.updateMorningExercise(newItem.copy(note = item.note))
                } else dataMorningDao.insertMorningComplete(item.toEntity())
            } else newItem?.let {
                dataMorningDao.deleteMorning(newItem)
            }
        }
    }
}