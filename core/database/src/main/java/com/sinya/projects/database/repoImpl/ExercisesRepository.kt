package com.sinya.projects.database.repoImpl

import com.sinya.projects.database.dao.ExercisesDao
import com.sinya.projects.database.mapper.toDomain
import com.sinya.projects.database.mapper.toEntity
import com.sinya.projects.domain.model.DropdownCheckboxItem
import com.sinya.projects.domain.model.ExerciseDescriptionEntry
import com.sinya.projects.domain.model.ExerciseDialogContent
import com.sinya.projects.domain.model.ExerciseMusclesData
import com.sinya.projects.domain.model.ExerciseMusclesEntry
import com.sinya.projects.domain.model.ExerciseTranslationsEntry
import com.sinya.projects.domain.model.ExerciseWithFullData
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.model.ExercisesEntry
import com.sinya.projects.domain.repository.ExercisesRepository
import com.sinya.projects.domain.repository.LanguageProvider
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExercisesRepositoryImpl @Inject constructor(
    private val exercisesDao: ExercisesDao,
    private val languageProvider: LanguageProvider
) : ExercisesRepository {

    // Exercises

    override fun observeExerciseTranslations(): Flow<List<ExerciseWithMuscles>> {
        return languageProvider.currentLanguage.flatMapLatest { language ->
            exercisesDao.observeExerciseList(language)
                .combine(
                    exercisesDao.observeMusclesForAllExercises(language)
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
        }.catch {
            emit(emptyList())
        }
    }

    override suspend fun deleteCustomExercise(id: Int): Result<Int> {
        return runCatching {
            val rows = exercisesDao.deleteExercise(id)
            if (rows == 0) throw IllegalStateException("Нельзя удалить упражнение: либо его не существует, либо оно не кастомное")
            rows
        }
    }

    // ExercisePage and TrainingPage

    override fun observeExerciseById(id: Int): Flow<ExerciseWithFullData> {
        return languageProvider.currentLanguage.flatMapLatest { language ->
            exercisesDao.observeExerciseById(id, language)
        }
    }

    override suspend fun getExerciseById(id: Int): Result<ExerciseDialogContent> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            exercisesDao.getExerciseById(id, lang)
        }
    }

    override fun getExerciseMusclesById(id: Int): Flow<List<ExerciseMusclesData>> {
        return languageProvider.currentLanguage.flatMapLatest { language ->
            exercisesDao.getExerciseMuscleById(id, language)
        }
            .onStart { emit(emptyList()) }
            .catch { emit(emptyList()) }
    }

    // RoutineAnalyze

    override fun getExerciseMusclesByIds(id: Int): Flow<List<ExerciseMusclesData>> {
        return languageProvider.currentLanguage.flatMapLatest { language ->
            exercisesDao.getExerciseMuscleByIds(id, language)
        }
            .onStart { emit(emptyList()) }
            .catch { emit(emptyList()) }
    }

    // ExercisePage (EditForm)

    override suspend fun insertNewExercise(
        ex: ExercisesEntry,
        exData: ExerciseTranslationsEntry,
        exMuscles: List<ExerciseMusclesEntry>
    ): Result<Int> {
        return runCatching {
            exercisesDao.createCustomExercise(
                ex.toEntity(),
                exData.toEntity(),
                exMuscles.map { it.toEntity() }
            )
        }
    }

    override suspend fun getForces(): Result<List<ExerciseDescriptionEntry>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            exercisesDao.getForces(lang).map { it.toDomain() }
        }
    }

    override suspend fun getLevels(): Result<List<ExerciseDescriptionEntry>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            exercisesDao.getLevels(lang).map { it.toDomain() }
        }
    }

    override suspend fun getMechanics(): Result<List<ExerciseDescriptionEntry>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            exercisesDao.getMechanics(lang).map { it.toDomain() }
        }
    }

    override suspend fun getEquipments(): Result<List<ExerciseDescriptionEntry>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            exercisesDao.getEquipments(lang).map { it.toDomain() }
        }
    }

    override suspend fun getCategories(): Result<List<ExerciseDescriptionEntry>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            exercisesDao.getCategories(lang).map { it.toDomain() }
        }
    }

    override suspend fun getMuscles(): Result<List<ExerciseMusclesData>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            exercisesDao.getMuscles(lang)
        }
    }

    override suspend fun getExercises(): Result<List<DropdownCheckboxItem>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            exercisesDao.getExercisesTitles(lang)
        }
    }

    override suspend fun getExercisesWithTranslations(): Result<List<ExerciseWithMuscles>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            val exercises = exercisesDao.getExerciseList(lang)
            val muscles = exercisesDao.getMusclesForAllExercises(lang)
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
    }

    override suspend fun checkNameExerciseExists(name: String, id: Int): Result<Boolean> {
        return runCatching {
            exercisesDao.checkIfNameExists(name, id)
        }
    }
}

