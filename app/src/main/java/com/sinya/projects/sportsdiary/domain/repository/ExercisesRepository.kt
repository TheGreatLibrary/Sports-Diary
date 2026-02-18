package com.sinya.projects.sportsdiary.domain.repository

import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.data.database.dao.ExercisesDao
import com.sinya.projects.sportsdiary.data.database.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.data.database.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseMuscles
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.data.database.entity.ForceTranslation
import com.sinya.projects.sportsdiary.data.database.entity.LevelTranslation
import com.sinya.projects.sportsdiary.data.database.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.domain.model.ExerciseMusclesData
import com.sinya.projects.sportsdiary.domain.model.ExerciseWithFullData
import com.sinya.projects.sportsdiary.domain.model.ExerciseWithMuscles
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart

interface ExercisesRepository {
    // Exercises

    fun observeExerciseTranslations(language: String = Locale.current.language): Flow<List<ExerciseWithMuscles>>

    suspend fun deleteCustomExercise(id: Int): Result<Int>

    // ExercisePage and TrainingPage

    fun getExerciseMusclesById(
        id: Int,
        language: String = Locale.current.language
    ): Flow<List<ExerciseMusclesData>>

    fun getExerciseById(
        id: Int,
        language: String = Locale.current.language
    ): Flow<ExerciseWithFullData>


    // ExercisePage (EditForm)

    suspend fun insertNewExercise(
        ex: Exercises,
        exData: ExerciseTranslations,
        exMuscles: List<ExerciseMuscles>
    ): Result<Int>

    suspend fun checkNameExerciseExists(name: String, id: Int): Result<Boolean>
    suspend fun getForces(language: String = Locale.current.language): Result<List<ForceTranslation>>
    suspend fun getLevels(language: String = Locale.current.language): Result<List<LevelTranslation>>
    suspend fun getMechanics(language: String = Locale.current.language): Result<List<MechanicTranslation>>
    suspend fun getEquipments(language: String = Locale.current.language): Result<List<EquipmentTranslation>>
    suspend fun getCategories(language: String = Locale.current.language): Result<List<CategoryTranslation>>
    suspend fun getMuscles(language: String = Locale.current.language): Result<List<ExerciseMusclesData>>
}

class ExercisesRepositoryImpl @Inject constructor(
    private val exercisesDao: ExercisesDao
) : ExercisesRepository {

    // Exercises

    override fun observeExerciseTranslations(language: String): Flow<List<ExerciseWithMuscles>> {
        return exercisesDao.observeExerciseList(language)
            .combine(
                exercisesDao.getMusclesForAllExercises(language)
            ) { exercises, muscles ->
                val allMusclesMap = muscles.groupBy { it.exerciseId }

                exercises.map { exercise ->
                    ExerciseWithMuscles(
                        id = exercise.id,
                        name = exercise.name,
                        isCustom = exercise.isCustom,
                        level = exercise.level,
                        equipment = exercise.equipment,
                        category = exercise.category,
                        muscles = allMusclesMap[exercise.id]
                            ?.map { it.name }
                            ?: emptyList()
                    )
                }
            }
            .catch {
                emit(emptyList())
            }
    }

    override suspend fun deleteCustomExercise(id: Int): Result<Int> {
        return try {
            val rows = exercisesDao.deleteExercise(id)

            if (rows == 0) throw IllegalStateException("Нельзя удалить упражнение: либо его не существует, либо оно не кастомное")

            Result.success(rows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ExercisePage and TrainingPage

    override fun getExerciseById(id: Int, language: String): Flow<ExerciseWithFullData> {
        return exercisesDao.getExerciseById(id, language)
    }

    override fun getExerciseMusclesById(id: Int, language: String): Flow<List<ExerciseMusclesData>> {
        return exercisesDao.getExerciseMuscleById(id, language)
            .onStart { emit(emptyList()) }
            .catch { emit(emptyList()) }
    }

    // ExercisePage (EditForm)

    override suspend fun insertNewExercise(
        ex: Exercises,
        exData: ExerciseTranslations,
        exMuscles: List<ExerciseMuscles>
    ): Result<Int> {
        return try {
            Result.success(exercisesDao.createCustomExercise(ex, exData, exMuscles))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getForces(language: String): Result<List<ForceTranslation>> {
        return try {
            Result.success(exercisesDao.getForces(language))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLevels(language: String): Result<List<LevelTranslation>> {
        return try {
            Result.success(exercisesDao.getLevels(language))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMechanics(language: String): Result<List<MechanicTranslation>> {
        return try {
            Result.success(exercisesDao.getMechanics(language))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEquipments(language: String): Result<List<EquipmentTranslation>> {
        return try {
            Result.success(exercisesDao.getEquipments(language))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(language: String): Result<List<CategoryTranslation>> {
        return try {
            Result.success(exercisesDao.getCategories(language))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMuscles(language: String): Result<List<ExerciseMusclesData>> {
        return try {
            Result.success(exercisesDao.getMuscles(language))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkNameExerciseExists(name: String, id: Int): Result<Boolean> {
        return try {
            Result.success(exercisesDao.checkIfNameExists(name, id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

