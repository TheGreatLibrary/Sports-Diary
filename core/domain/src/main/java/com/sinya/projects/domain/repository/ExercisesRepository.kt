package com.sinya.projects.domain.repository

import com.sinya.projects.domain.model.DropdownCheckboxItem
import com.sinya.projects.domain.model.ExerciseDescriptionEntry
import com.sinya.projects.domain.model.ExerciseDialogContent
import com.sinya.projects.domain.model.ExerciseMusclesData
import com.sinya.projects.domain.model.ExerciseMusclesEntry
import com.sinya.projects.domain.model.ExerciseTranslationsEntry
import com.sinya.projects.domain.model.ExerciseWithFullData
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.domain.model.ExercisesEntry
import kotlinx.coroutines.flow.Flow

interface ExercisesRepository {
    // Exercises

    fun observeExerciseTranslations(): Flow<List<ExerciseWithMuscles>>

    suspend fun deleteCustomExercise(id: Int): Result<Int>

    // ExercisePage and TrainingPage

    fun getExerciseMusclesById(
        id: Int,
    ): Flow<List<ExerciseMusclesData>>

    fun observeExerciseById(
        id: Int,
    ): Flow<ExerciseWithFullData>

    suspend fun getExerciseById(
        id: Int,
    ): Result<ExerciseDialogContent>


    // ExercisePage (EditForm)

    suspend fun insertNewExercise(
        ex: ExercisesEntry,
        exData: ExerciseTranslationsEntry,
        exMuscles: List<ExerciseMusclesEntry>
    ): Result<Int>


    suspend fun checkNameExerciseExists(name: String, id: Int): Result<Boolean>
    suspend fun getForces(): Result<List<ExerciseDescriptionEntry>>
    suspend fun getLevels(): Result<List<ExerciseDescriptionEntry>>
    suspend fun getMechanics(): Result<List<ExerciseDescriptionEntry>>
    suspend fun getEquipments(): Result<List<ExerciseDescriptionEntry>>
    suspend fun getCategories(): Result<List<ExerciseDescriptionEntry>>
    suspend fun getMuscles(): Result<List<ExerciseMusclesData>>
    suspend fun getExercises(): Result<List<DropdownCheckboxItem>>
    suspend fun getExercisesWithTranslations(): Result<List<ExerciseWithMuscles>>
    fun getExerciseMusclesByIds(id: Int): Flow<List<ExerciseMusclesData>>
}

