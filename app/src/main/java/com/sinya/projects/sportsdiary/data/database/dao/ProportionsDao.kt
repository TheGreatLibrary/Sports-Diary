package com.sinya.projects.sportsdiary.data.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.sinya.projects.sportsdiary.data.database.entity.DataProportions
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.domain.model.ProportionDialogContent
import com.sinya.projects.sportsdiary.domain.model.ProportionItem
import com.sinya.projects.sportsdiary.domain.model.ProportionRow

@Dao
interface ProportionsDao {

    // Proportions

    @Query(
        """
        SELECT 
            p.id as id,
             COALESCE(
            strftime('%Y-%m-%d', p.date),
            strftime('%Y-%m-%d', 'now','localtime')
        ) AS date
        FROM proportions p
    """
    )
    suspend fun getProportionsList(): List<Proportions>

    @Delete
    suspend fun deleteProportion(it: Proportions): Int

    // ProportionPage

    @Query(
        """
      SELECT 
        t.id,
        tpt.name AS title,
        COALESCE(d.value, 0) as value,
        u.name AS unitMeasure,
        d_prev.value AS delta
      FROM data_proportions d
      JOIN type_proportions t ON t.id = d.type_id
      JOIN units_measurement u ON u.id = t.unit_measure_id
      JOIN type_proportion_translations tpt ON t.id = tpt.type_id
        LEFT JOIN data_proportions d_prev
        ON d_prev.type_id = d.type_id
       AND d_prev.proportion_id = (
            SELECT id
            FROM proportions
            WHERE id < :id
            ORDER BY id DESC
            LIMIT 1
       )
      WHERE d.proportion_id = :id AND language = :locale
      ORDER BY t.id
    """
    )
    suspend fun proportionPage(id: Int, locale: String): List<ProportionRow>

    @Query(
        """
        SELECT 
            t.id,
            tpt.name AS title,
            COALESCE(d.value, 0) as value,
            u.name AS unitMeasure,
            d.value as delta
        FROM type_proportions t
        LEFT JOIN data_proportions d ON t.id = d.type_id AND d.proportion_id IN (SELECT id FROM proportions ORDER BY id DESC LIMIT 1) 
        JOIN units_measurement u ON u.id = t.unit_measure_id
        JOIN type_proportion_translations tpt ON t.id = tpt.type_id
        WHERE  language = :language
        ORDER BY t.id
    """
    )
    suspend fun newProportionsWithPrevData(language: String): List<ProportionRow>

    @Query("SELECT * FROM proportions WHERE id = :id")
    suspend fun getById(id: Int): Proportions?

    @Transaction
    suspend fun insertOrUpdate(item: ProportionItem): Int {
        return if (item.id == null) {
            val newId = upsertProportion(
                Proportions(date = item.date)
            ).toInt()

            insertDataProportions(
                item.items.map {
                    DataProportions(
                        proportionId = newId,
                        typeId = it.id,
                        value = it.value.toFloat()
                    )
                }
            )

            newId
        } else {
            upsertProportion(
                Proportions(id = item.id, date = item.date)
            ).toInt()

            updateProportion(
                item.items.map {
                    DataProportions(
                        proportionId = item.id,
                        typeId = it.id,
                        value = (it.value.ifEmpty { "0" }).toFloat()
                    )
                }
            )

            item.id
        }
    }

    @Upsert
    suspend fun upsertProportion(item: Proportions): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDataProportions(item: List<DataProportions>): List<Long>

    @Update
    suspend fun updateProportion(item: List<DataProportions>): Int

    @Query(
        """
        SELECT 
            type_id AS id,
            name,
            description,
            icon
        FROM type_proportion_translations tpt JOIN type_proportions tp ON tpt.type_id = tp.id
        WHERE type_id = :id AND language = :locale
    """
    )
    suspend fun getMeasurementDataById(id: Int, locale: String): ProportionDialogContent
}