package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining

@Dao
interface TypeTrainingDao {

    @Query("SELECT * FROM type_training")
    suspend fun getList(): List<TypeTraining>

    @Upsert
    suspend fun insertType(type: TypeTraining): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(refs: List<DataTypeTrainings>): List<Long>

    @Transaction
    suspend fun createTypeWithExercises(name: String, exerciseIds: List<DataTypeTrainings>): Long {
        val typeId = insertType(TypeTraining(name = name))

        if (typeId != -1L) {
            insertCrossRefs(exerciseIds.map {
                it.copy(typeId = typeId.toInt())
            })
        }

        return typeId
    }

    @Query(
        """
        SELECT *
        FROM type_training
        WHERE id = :i
        LIMIT 1
    """
    )
    suspend fun getById(i: Int): TypeTraining?
}