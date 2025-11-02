package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseMuscles
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.data.database.repository.ExerciseMusclesData
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi

@Dao
interface ExercisesDao {

    @Query("""
        SELECT 
            e.id, 
            et.name, 
            0 as checked 
        FROM exercises e
        JOIN exercise_translations et ON e.id = et.exercise_id
        WHERE language = :locale
        ORDER BY name
    """)
    suspend fun getExercisesList(locale: String): List<ExerciseUi>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercises): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercises(exercises: List<Exercises>): List<Long>

    @Query("SELECT * FROM exercises")
    suspend fun getAllExercises(): List<Exercises>

    @Query("SELECT * FROM exercise_translations WHERE exercise_id = :exerciseId AND language = :language LIMIT 1")
    suspend fun getExerciseById(exerciseId: Int, language: String): ExerciseTranslations
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
        translations: List<ExerciseTranslations>,
        exerciseMuscles: List<ExerciseMuscles>
    ) {
        insertExercises(exercises)
        insertExerciseMuscle(exerciseMuscles)
        insertExerciseTranslations(translations)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseMuscle(exerciseMuscle: List<ExerciseMuscles>)

    @Query("""
        SELECT *
        FROM exercise_translations
        WHERE language = :language
        ORDER BY name
    """)
    suspend fun getExercisesTranslations(language: String): List<ExerciseTranslations>

    @Query("""
        SELECT 
            em.muscle_id AS muscleId,
            exercise_id AS exerciseId,
            language,
            name,
            value
        FROM exercise_muscles em JOIN muscle_translations mt ON em.muscle_id = mt.muscle_id
        WHERE exercise_id = :id AND language = :language
    """)
    suspend fun getExerciseMuscleById(id: Int, language: String): List<ExerciseMusclesData>
}