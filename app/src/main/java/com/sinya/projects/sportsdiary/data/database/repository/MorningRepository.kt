package com.sinya.projects.sportsdiary.data.database.repository

import com.sinya.projects.sportsdiary.data.database.dao.DataMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.PlanMorningDao
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.data.database.entity.PlanMornings
import com.sinya.projects.sportsdiary.presentation.home.MorningDay
import jakarta.inject.Inject

interface MorningRepository {
    suspend fun getList(start: String, end: String) : List<MorningDay>
    suspend fun getNotes() : List<DataMorning>
    suspend fun insertMorning(item: DataMorning)
    suspend fun updateMorning(item: DataMorning)
    suspend fun getCount(): Int

    suspend fun getPlans() : List<PlanMornings>
    suspend fun insertPlanMorning(item: PlanMornings)
    suspend fun updatePlanMorning(item: PlanMornings)
    suspend fun deletePlanMorning(item: PlanMornings)
}

class MorningRepositoryImpl @Inject constructor(
    private val dataMorningDao: DataMorningDao,
    private val planMorningDao: PlanMorningDao
) : MorningRepository {
    override suspend fun getList(start: String, end: String): List<MorningDay> {
        return dataMorningDao.getDataOfMonth(start, end)
    }

    override suspend fun getNotes(): List<DataMorning> {
        return dataMorningDao.getNotes().filter { !it.note.isNullOrEmpty() }
    }

    override suspend fun insertMorning(item: DataMorning) {
        dataMorningDao.insertOrUpdateMorningExercises(item)
    }

    override suspend fun updateMorning(item: DataMorning) {
        dataMorningDao.updateMorningExercise(item)
    }

    override suspend fun getCount(): Int {
        return dataMorningDao.getCount()
    }


    override suspend fun getPlans(): List<PlanMornings> {
        return planMorningDao.getListPlan()
    }

    override suspend fun insertPlanMorning(item: PlanMornings) {
        planMorningDao.insertPlan(item)
    }

    override suspend fun updatePlanMorning(item: PlanMornings) {
        planMorningDao.updatePlan(item)

    }

    override suspend fun deletePlanMorning(item: PlanMornings) {
        planMorningDao.deletePlan(item)
    }
}