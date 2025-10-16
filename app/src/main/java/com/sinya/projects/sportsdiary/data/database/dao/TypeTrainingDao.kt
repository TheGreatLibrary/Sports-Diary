package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.presentation.trainingPage.ExerciseItemOfList

@Dao
interface TypeTrainingDao {

    @Query("SELECT * FROM type_training")
    suspend fun getList(): List<TypeTraining>

//    @Query("SELECT id, name FROM exercises ORDER BY name")
//    suspend fun getAllExercises(): List<ExerciseItemOfList>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertType(type: TypeTraining): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(refs: List<DataTypeTrainings>)

    @Transaction
    suspend fun createTypeWithExercises(name: String, exerciseIds: List<Int>) {
        val typeId = insertType(TypeTraining(name = name)).toInt()
        val refs = exerciseIds.map { DataTypeTrainings(typeId, it) }
        insertCrossRefs(refs)
    }
}