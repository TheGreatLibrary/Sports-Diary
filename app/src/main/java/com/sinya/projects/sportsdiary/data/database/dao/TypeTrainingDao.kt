package com.sinya.projects.sportsdiary.data.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining

@Dao
interface TypeTrainingDao {

    // Categories

    @Delete
    suspend fun delete(it: TypeTraining): Int

    @Query("SELECT * FROM type_training")
    suspend fun getList(): List<TypeTraining>

    // CategoryPage

    @Query(
        """
        SELECT *
        FROM type_training
        WHERE id = :i
        LIMIT 1
    """
    )
    suspend fun getById(i: Int?): TypeTraining?

    @Transaction
    suspend fun createTypeWithExercises(item: TypeTraining, exerciseIds: List<DataTypeTrainings>): Int {
        val typeId = upsertCategory(item).let { returned ->
            if (returned == -1L) item.id
            else returned.toInt()
        }

        deleteDataTypeTrainingByTypeId(typeId)

        if(exerciseIds.isNotEmpty() && typeId!=1) {
            Log.d("D",insertDataTypeTrainings(exerciseIds.map {
                it.copy(typeId = typeId)
            }).toString())
        }

        return typeId
    }

    @Upsert
    suspend fun upsertCategory(type: TypeTraining): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDataTypeTrainings(refs: List<DataTypeTrainings>): List<Long>

    @Query("DELETE FROM data_type_trainings WHERE type_id = :typeId")
    suspend fun deleteDataTypeTrainingByTypeId(typeId: Int)

}