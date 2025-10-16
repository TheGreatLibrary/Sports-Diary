package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.presentation.home.MorningDay

@Dao
interface DataMorningDao {

    @Query("""
        SELECT id, date 
        FROM data_mornings
        WHERE date >= :startDate AND date < :endDate
        ORDER BY date
    """)
    suspend fun getDataOfMonth(startDate: String, endDate: String) : List<MorningDay>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMorningComplete(item: DataMorning) : Long
}

