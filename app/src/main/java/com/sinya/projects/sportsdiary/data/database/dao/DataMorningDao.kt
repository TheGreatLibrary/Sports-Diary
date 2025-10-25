package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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

    @Update
    suspend fun updateMorningExercise(item: DataMorning)

    @Transaction
    suspend fun insertOrUpdateMorningExercises(item: DataMorning) {
        val itemInDB = getByDate(item.date)
        if (itemInDB!=null) {
            updateMorningExercise(itemInDB.copy(note = item.note))
        }
        else insertMorningComplete(item)
    }

    @Query("SELECT * FROM data_mornings WHERE date = :date")
    suspend fun getByDate(date: String) : DataMorning?

    @Query("SELECT COUNT(*) FROM data_mornings")
    suspend fun getCount(): Int

    @Query("""
        SELECT COUNT(id)
        FROM data_mornings
    """)
    suspend fun getSeriesScope(): Int

    @Query("SELECT * FROM data_mornings")
    suspend fun getNotes(): List<DataMorning>
}

