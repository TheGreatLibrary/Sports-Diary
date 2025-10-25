package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sinya.projects.sportsdiary.data.database.entity.PlanMornings

@Dao
interface PlanMorningDao {

    @Query("SELECT * FROM plan_mornings")
    suspend fun getListPlan() : List<PlanMornings>

//    @Insert
//    suspend fun insertPlan()
//
//    @Update
//    suspend fun updatePlan()
//
//    @Transaction
//    suspend fun insertOrUpdate() {
//
//    }
}