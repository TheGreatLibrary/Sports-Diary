package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetCategory.ExerciseUi

@Dao
interface ExercisesDao {

    @Query("""
        SELECT 
            e.id, 
            et.name, 
            0 as checked 
        FROM exercises e
        JOIN exercise_translations et ON e.id = et.exercise_id
    """)
    suspend fun getExercisesList(): List<ExerciseUi>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercises): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercises(exercises: List<Exercises>): List<Long>

    @Query("SELECT * FROM exercises")
    suspend fun getAllExercises(): List<Exercises>

    @Query("SELECT * FROM exercise_translations WHERE exercise_id = :exerciseId LIMIT 1")
    suspend fun getExerciseById(exerciseId: Int): ExerciseTranslations
//
//    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%'")
//    suspend fun searchByName(query: String): List<Exercises>

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM exercise_translations")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseTranslations(exercisesTranslations: List<ExerciseTranslations>)

    @Transaction
    suspend fun insertExercisesData(
        exercises: List<Exercises>,
        translations: List<ExerciseTranslations>
    ) {
        insertExercises(exercises)
        insertExerciseTranslations(translations)
    }

    @Query("""
        SELECT *
        FROM exercise_translations
    """)
    suspend fun getExercisesTranslations(): List<ExerciseTranslations>
}