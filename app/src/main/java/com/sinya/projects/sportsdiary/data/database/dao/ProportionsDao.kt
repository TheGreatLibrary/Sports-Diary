package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sinya.projects.sportsdiary.data.database.entity.DataProportions
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionItem
import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionRow
import com.sinya.projects.sportsdiary.presentation.proportions.Proportion

@Dao
interface ProportionsDao {

    @Query("""
        SELECT 
            p.id as id,
            p.id as title,
             COALESCE(
            strftime('%d/%m/%Y', p.date),
            strftime('%d/%m/%Y', 'now','localtime')
          ) AS date
        FROM proportions p
    """)
    suspend fun getProportionsList() : List<Proportion>

    @Query("""
      SELECT 
        t.id,
        tpt.name AS title,
        COALESCE(d.value, 0) as value,
        u.name AS unitMeasure
      FROM data_proportions d
      JOIN type_proportions t ON t.id = d.type_id
      JOIN units_measurement u ON u.id = t.unit_measure_id
      JOIN type_proportion_translations tpt ON t.id = tpt.type_id
      WHERE d.proportion_id = :id
      ORDER BY t.id
    """)
    suspend fun proportionPage(id: Int): List<ProportionRow>

    @Query("""
      SELECT 
        t.id,
        tpt.name AS title,
        0 as value,
        u.name AS unitMeasure
      FROM type_proportions t
      JOIN units_measurement u ON u.id = t.unit_measure_id
    JOIN type_proportion_translations tpt ON t.id = tpt.type_id
      ORDER BY t.id
    """)
    suspend fun newProportions(): List<ProportionRow>



    @Query("""
    SELECT
      COALESCE(:id, (SELECT COALESCE(MAX(p2.id),0)+1 FROM proportions p2)) AS id,
      strftime('%d/%m/%Y',
               COALESCE((SELECT p3.date FROM proportions p3 WHERE p3.id = :id),
                        'now','localtime')
      ) AS date
    """)
    suspend fun getById(id: Int?): Proportions

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProportion(item: Proportions) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDataProportions(item: List<DataProportions>)

    @Transaction
    suspend fun insertOrUpdate(item: ProportionItem) {
        if (getById(item.id).id==item.id) {
            val rowId = insertProportion(Proportions(item.id, item.date)).toInt()
            insertDataProportions(item.items.map { DataProportions(
                proportionId = rowId,
                typeId = it.id,
                value = it.value.toInt()
            ) })
        }
        else updateProportion(item.items.map { DataProportions(
            proportionId = item.id,
            typeId = it.id,
            value = it.value.toInt()

        ) }
        )
    }

    @Update
    suspend fun updateProportion(item: List<DataProportions>)
}