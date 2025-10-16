package com.sinya.projects.sportsdiary.data.database.repository

import android.provider.ContactsContract.Data
import com.sinya.projects.sportsdiary.data.database.dao.DataMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ExercisesDao
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.presentation.home.MorningDay
import jakarta.inject.Inject

interface MorningRepository {
    suspend fun getList(start: String, end: String) : List<MorningDay>
    suspend fun insertMorning(item: DataMorning)
}

class MorningRepositoryImpl @Inject constructor(
    private val dataMorningDao: DataMorningDao
) : MorningRepository {
    override suspend fun getList(start: String, end: String): List<MorningDay> {
        return dataMorningDao.getDataOfMonth(start, end)
    }

    override suspend fun insertMorning(item: DataMorning) {
        dataMorningDao.insertMorningComplete(item)
    }


}