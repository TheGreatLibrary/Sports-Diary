package com.sinya.projects.sportsdiary.core.data.dataBase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItemWithoutList
import kotlinx.coroutines.flow.Flow

@Dao
interface TypeTrainingDao {

    // Categories

    @Delete
    suspend fun delete(it: TypeTraining): Int

    @Query("SELECT * FROM type_training")
    fun getList(): Flow<List<TypeTraining>>

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

    @Query(
        """
        SELECT *
        FROM type_training
        WHERE id = :i
        LIMIT 1
    """
    )
    fun observeTypeTrainingById(i: Int?): Flow<TypeTraining?>

    @Transaction
    suspend fun createTypeWithExercises(item: TypeTraining, exerciseIds: List<DataTypeTrainings>): Int {
        val typeId = upsertCategory(item.copy(name = item.name.trim())).let { returned ->
            if (returned == -1L) item.id
            else returned.toInt()
        }

        deleteDataTypeTrainingByTypeId(typeId)

        if(exerciseIds.isNotEmpty()) {
            insertDataTypeTrainings(exerciseIds.map {
                it.copy(typeId = typeId)
            })
        }

        return typeId
    }

    @Upsert
    suspend fun upsertCategory(type: TypeTraining): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDataTypeTrainings(refs: List<DataTypeTrainings>): List<Long>

    @Query("DELETE FROM data_type_trainings WHERE type_id = :typeId")
    suspend fun deleteDataTypeTrainingByTypeId(typeId: Int)

    @Query("SELECT COUNT(*) FROM type_training WHERE name = :name AND id != :excludeId")
    suspend fun checkIfNameExists(name: String, excludeId: Int): Boolean



    @Query("""
      SELECT 
        e.id,
        et.name AS title,
        d.mode AS mode,
        d.weight AS weight,
        d.order_index AS orderIndex
      FROM data_type_trainings d
      JOIN exercises e ON e.id = d.exercise_id
      JOIN exercise_translations et ON e.id = et.exercise_id
      WHERE d.type_id = :id AND et.language = :lang
    """)
    fun observeItemsTypeTraining(id: Int?, lang: String): Flow<List<ExerciseItemWithoutList>>
}