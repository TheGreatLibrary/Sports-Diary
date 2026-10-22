package com.sinya.projects.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sinya.projects.domain.model.MorningDay
import com.sinya.projects.database.entity.DataMorning
import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.model.NoteItem
import kotlinx.coroutines.flow.Flow

@Dao
internal interface DataMorningDao {

    @Query("""
        SELECT id, date, note
        FROM data_mornings
        WHERE date >= :startDate AND date <= :endDate
        ORDER BY date
    """)
    fun getDataOfMonth(startDate: String, endDate: String) : Flow<List<MorningDay>>

    @Query("""
    SELECT id, date, note
    FROM data_mornings
    ORDER BY date DESC
""")
    suspend fun getAllMornings(): List<MorningDay>


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMorningComplete(item: DataMorning) : Long

    @Update
    suspend fun updateMorningExercise(item: DataMorning) : Int

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
       SELECT id, note, date, 0 AS selected, 0 AS edit
        FROM data_mornings 
        WHERE note IS NOT NULL AND note != ''
    """)
    fun getNotes(): Flow<List<NoteItem>>

    @Delete
    suspend fun deleteMorning(item: DataMorning)

    @Query("SELECT * FROM data_mornings WHERE date IN (:dates)")
    suspend fun getByDates(dates: List<String>): List<DataMorning> // Тип List<String> или List<Long>, смотря как хранишь

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMorningList(items: List<DataMorning>)

    @Update
    suspend fun updateMorningList(items: List<DataMorning>)

    @Delete
    suspend fun deleteMorningList(items: List<DataMorning>)

    @Query("""
        UPDATE data_mornings 
        SET note = :note
        WHERE id = :id
    """)
    suspend fun editNote(id: Int, note: String?): Int

    @Query("""
        UPDATE data_mornings 
        SET note = ''
        WHERE id IN (:list)
    """)
    suspend fun deleteNotes(list: List<Int>): Int
}

