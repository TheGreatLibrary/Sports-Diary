package com.sinya.projects.sportsdiary.core.data.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ExerciseMuscles
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Exercises
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ForceTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.LevelTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseMuscleName
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseMusclesData
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithFullData
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithSortedData
import kotlinx.coroutines.flow.Flow

@Dao
interface ExercisesDao {

    // ExercisePage (EditForm)

    @Query("SELECT COUNT(*) FROM exercise_translations WHERE name = :name AND exercise_id != :excludeId")
    suspend fun checkIfNameExists(name: String, excludeId: Int): Boolean

    @Upsert
    suspend fun upsertExercise(exercise: Exercises): Long

    @Upsert
    suspend fun insertExerciseTranslations(exerciseTranslations: ExerciseTranslations)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExerciseMuscles(refs: List<ExerciseMuscles>): List<Long>

    @Query("DELETE FROM exercise_muscles WHERE exercise_id = :exId")
    suspend fun deleteMusclesByExerciseId(exId: Int)

    @Transaction
    suspend fun createCustomExercise(
        exercise: Exercises,
        translations: ExerciseTranslations,
        muscles: List<ExerciseMuscles>
    ): Int {
        require(exercise.isCustom) { "Only custom exercises can be created through this method" }

        val exerciseId = upsertExercise(exercise).let { returned ->
            if (returned == -1L) exercise.id
            else returned.toInt()
        }

        insertExerciseTranslations(translations.copy(exerciseId = exerciseId))

        deleteMusclesByExerciseId(exerciseId)
        if (muscles.isNotEmpty()) {
            insertExerciseMuscles(
                muscles.map { it.copy(exerciseId = exerciseId) }
            )
        }

        return exerciseId
    }

    @Query("SELECT * FROM force_translation WHERE language = :language")
    suspend fun getForces(language: String): List<ForceTranslation>

    @Query("SELECT * FROM equipment_translation WHERE language = :language")
    suspend fun getEquipments(language: String): List<EquipmentTranslation>

    @Query("SELECT * FROM mechanic_translation WHERE language = :language")
    suspend fun getMechanics(language: String): List<MechanicTranslation>

    @Query("SELECT * FROM category_translation WHERE language = :language")
    suspend fun getCategories(language: String): List<CategoryTranslation>

    @Query("SELECT * FROM level_translation WHERE language = :language")
    suspend fun getLevels(language: String): List<LevelTranslation>

    @Query("""
        SELECT 
            m.id AS muscleId,
            name,
            0 AS value,
            0 AS checked
        FROM muscles m JOIN muscle_translations mt ON m.id = mt.muscle_id
        WHERE language = :language
    """)
    suspend fun getMuscles(language: String): List<ExerciseMusclesData>

    // ExercisePage

    @Query(
        """
        SELECT 
            exercise_id as id,
            et.name,
            et.description,
            et.rule,
            e.is_custom AS isCustom,
            f.name as force,
            l.name AS level,
            m.name AS mechanic,
            eq.name AS equipment,
            c.name AS category
        FROM exercise_translations et 
            JOIN exercises e ON et.exercise_id = e.id 
            LEFT JOIN equipment_translation eq ON eq.eq_id = e.equipment_id AND eq.language = :language 
            LEFT JOIN category_translation c ON c.category_id = e.category_id AND c.language = :language 
            LEFT JOIN level_translation l ON l.level_id = e.level_id AND l.language = :language
            LEFT JOIN force_translation f ON f.force_id = e.force_id AND f.language = :language
            LEFT JOIN mechanic_translation m ON m.mechanic_id = e.mechanic_id AND m.language = :language
        WHERE et.exercise_id = :exerciseId
        ORDER BY CASE WHEN et.language = :language THEN 0 ELSE 1 END
        LIMIT 1 
    """
    )
    fun getExerciseById(exerciseId: Int, language: String): Flow<ExerciseWithFullData>

    @Query("""
        SELECT 
            em.muscle_id AS muscleId,
            name,
            value,
            0 AS checked
        FROM exercise_muscles em JOIN muscle_translations mt ON em.muscle_id = mt.muscle_id
        WHERE exercise_id = :id AND language = :language
    """)
    fun getExerciseMuscleById(id: Int, language: String): Flow<List<ExerciseMusclesData>>

    // Exercises

    @Query("""
        DELETE FROM exercises
        WHERE id = :id AND is_custom == 1
    """)
    suspend fun deleteExercise(id: Int): Int

    @Query("""
        SELECT 
            exercise_id as id,
            et.name,
            e.is_custom AS isCustom,
            l.name AS level,
            eq.name AS equipment,
            c.name AS category
        FROM exercise_translations et 
            JOIN exercises e ON et.exercise_id = e.id 
            LEFT JOIN equipment_translation eq ON eq.eq_id = e.equipment_id AND eq.language = :language 
            LEFT JOIN category_translation c ON c.category_id = e.category_id AND c.language = :language 
            LEFT JOIN level_translation l ON l.level_id = e.level_id AND l.language = :language
        WHERE et.language = :language
        ORDER BY et.name, is_custom DESC
    """)
    fun observeExerciseList(language: String): Flow<List<ExerciseWithSortedData>>

    @Query("""
        SELECT 
            em.exercise_id AS exerciseId,
            mt.name as name
        FROM exercise_muscles em 
        JOIN muscle_translations mt ON em.muscle_id = mt.muscle_id
        WHERE mt.language = :language
        ORDER BY em.exercise_id
    """)
    fun getMusclesForAllExercises(language: String): Flow<List<ExerciseMuscleName>>
}