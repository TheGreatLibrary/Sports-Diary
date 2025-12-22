package com.sinya.projects.sportsdiary.data.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining

@Dao
interface TypeTrainingDao {

    @Query("SELECT * FROM type_training")
    suspend fun getList(): List<TypeTraining>

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