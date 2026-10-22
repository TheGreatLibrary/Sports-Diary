package com.sinya.projects.domain.repository

import com.sinya.projects.domain.enums.TypeTime
import com.sinya.projects.domain.model.CategoryEntity
import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.model.DataTrainingEntry
import com.sinya.projects.domain.model.DataTypeTrainingsEntry
import com.sinya.projects.domain.model.ExerciseItem
import com.sinya.projects.domain.model.Training
import com.sinya.projects.domain.model.TrainingEntity
import com.sinya.projects.domain.model.TrainingsEntry
import com.sinya.projects.domain.model.TypeTrainingEntry
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    // Statistic
    suspend fun getCountOfTrainings(): Result<Int>
    suspend fun getSummaryWeightOfTrainings(): Result<Float>
    suspend fun getChartList(mode: TypeTime): Result<List<ChartLine>>

    // Training
    fun getTrainingList(): Flow<List<Training>>
    suspend fun deleteTrainings(items: List<TrainingsEntry>): Result<Int>

    // TrainingPage
    fun observeTrainingEntityById(id: Int, ): Flow<TrainingEntity>

    fun getNewTraining(date: String?): Result<TrainingEntity>


    suspend fun insertDataTraining(items: List<DataTrainingEntry>): Result<Int>
    suspend fun upsertTraining(entity: TrainingEntity): Result<Int>
    suspend fun getSerialNumOfCategory(typeTraining: Int?): Result<String>

    suspend fun getDataByTypeTraining(typeId: Int): Result<List<ExerciseItem>>

    // Calendar
    fun getList(start: String, end: String): Flow<List<Training>>

    // Categories
    suspend fun deleteCategory(it: List<TypeTrainingEntry>): Result<Int>
    fun observeCategoriesList(): Flow<List<TypeTrainingEntry>>

    // CategoryPage
    suspend fun insertCategory(item: TypeTrainingEntry, exercises: List<DataTypeTrainingsEntry>): Result<Int>
    suspend fun updateDataCategory(items: List<DataTypeTrainingsEntry>): Result<Int>
    suspend fun checkNameCategoryExists(name: String, id: Int): Result<Boolean>
    suspend fun getExercisesData(list: List<Int>): Result<List<ChartLine>>
    suspend fun getDataExerciseMuscles(from: String, to: String): Result<List<DataExerciseMuscles>>
    suspend fun getCategoryEntity(id: Int?): Result<CategoryEntity>
    suspend fun getPrevTrainingSummaryWeight(categoryId: Int?, trainingId: Int): Result<Float>

    suspend fun getMuscleDataById(id: Int, categoryId: Int?): Result<List<DataExerciseMuscles>>
}