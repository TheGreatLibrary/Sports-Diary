package com.sinya.projects.domain.repository

import com.sinya.projects.domain.model.MorningDay
import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.model.NoteItem
import kotlinx.coroutines.flow.Flow

interface MorningRepository {
    fun getList(start: String, end: String): Flow<List<MorningDay>>
    fun getNotes(): Flow<List<NoteItem>>
    suspend fun insertMorning(item: MorningEntry) : Result<Int>
    suspend fun getCount(): Result<Int>
    suspend fun getSeriesScopeMorning(): Result<Int>

    suspend fun deleteMorning(item: MorningEntry): Result<Int>
    suspend fun getMorningByDate(date: String): Result<MorningEntry?>
    suspend fun markDayMorning(morningState: Boolean, item: MorningEntry): Result<Unit>
    suspend fun markDaysListMorning(marked: Boolean, items: List<MorningEntry>): Result<Unit>
    suspend fun editNote(day: MorningEntry): Result<Unit>
    suspend fun deleteNotes(list: List<Int>): Result<Unit>
}

