package com.sinya.projects.sportsdiary.core.domain.repository

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TrainingsDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTraining
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Trainings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.core.domain.model.CategoryEntity
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItem
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItemData
import com.sinya.projects.sportsdiary.core.domain.model.Training
import com.sinya.projects.sportsdiary.core.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.core.domain.model.toItem
import com.sinya.projects.sportsdiary.ui.features.diagram.ChartPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.time.LocalDate

interface TrainingRepository {
    // Statistic
    suspend fun getCountOfTrainings(): Result<Int>
    suspend fun getSummaryWeightOfTrainings(): Result<Float>
    suspend fun getChartList(mode: TypeTime): Result<List<ChartPoint>>

    // Training
    fun getTrainingList(): Flow<List<Training>>
    suspend fun deleteTraining(it: Trainings): Result<Int>

    // TrainingPage
    suspend fun getById(id: Int?): Result<TrainingEntity>
    suspend fun insertDataTraining(items: List<DataTraining>): Result<Int>
    suspend fun upsertTraining(entity: TrainingEntity): Result<Int>
    suspend fun getSerialNumOfCategory(typeTraining: Int?): Result<String>
    suspend fun getDataByTypeTraining(typeId: Int): Result<List<ExerciseItem>>

    // Calendar
    fun getList(start: String, end: String): Flow<List<Training>>

    // Categories
    suspend fun deleteCategory(it: TypeTraining): Result<Int>
    fun getCategoriesList(): Flow<List<TypeTraining>>

    // CategoryPage

    suspend fun getCategoryEntity(id: Int?): Result<CategoryEntity>
    suspend fun insertCategory(item: TypeTraining, exercises: List<DataTypeTrainings>): Result<Int>
    suspend fun updateDataCategory(items: List<DataTypeTrainings>): Result<Int>
    suspend fun checkNameCategoryExists(name: String, id: Int): Result<Boolean>
}


class TrainingRepositoryImpl @Inject constructor(
    private val typeTrainingDao: TypeTrainingDao,
    private val trainingDao: TrainingsDao
) : TrainingRepository {

    // Statistic

    override suspend fun getCountOfTrainings(): Result<Int> {
        return try {
            val count = trainingDao.getCount()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSummaryWeightOfTrainings(): Result<Float> {
        return try {
            val data = trainingDao.getDataOfTraining()
            var weight = 0f

            data.forEach {
                val countArr = it.count.split('/')
                val weightArr = it.weight.split('/')

                for (i in countArr.indices) {
                    weight += countArr[i].toInt() * weightArr[i].toFloat()
                }
            }

            val result = weight / 1000

            val roundedResult = String.format(java.util.Locale.ENGLISH, "%.1f", result).toFloat()

            Result.success(roundedResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChartList(mode: TypeTime): Result<List<ChartPoint>> {
        return try {
            val raw = trainingDao.getChartList()

            val grouped = when (mode) {
                TypeTime.DAYS -> raw.groupBy { it.date } // yyyy-MM-dd
                TypeTime.MONTHS -> raw.groupBy { it.date.substring(0, 7) } // yyyy-MM
                TypeTime.YEARS -> raw.groupBy { it.date.substring(0, 4) }  // yyyy
            }

            val result = grouped.map { (key, entries) ->
                var totalWeight = 0f

                entries.forEach { item ->
                    val countArr = item.count.split('/')
                    val weightArr = item.weight.split('/')

                    for (i in countArr.indices) {
                        totalWeight += countArr[i].toInt() * weightArr[i].toFloat()
                    }
                }

                totalWeight /= 1000f

                val label = when (mode) {
                    TypeTime.DAYS -> LocalDate.parse(key).toString()
                    TypeTime.MONTHS -> LocalDate.parse("$key-01").toString()
                    TypeTime.YEARS -> LocalDate.parse("$key-01-01").toString()
                }

                ChartPoint(label, totalWeight)
            }.sortedBy { it.xDate }

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Training

    override fun getTrainingList(): Flow<List<Training>> {
        return trainingDao.getList().catch { emit(emptyList()) }
    }

    override suspend fun deleteTraining(it: Trainings): Result<Int> {
        return try {
            val result = trainingDao.deleteTraining(it)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // TrainingPage

    override suspend fun getDataByTypeTraining(typeId: Int): Result<List<ExerciseItem>> {
        val locale = Locale.current.language
        val lastTrainingItems = trainingDao.getExercisesByCategoryIdWithLastData(typeId, locale)

        return try {
            val list =
                if (lastTrainingItems.isNotEmpty()) lastTrainingItems.map { it.toItem() }
                else {
                    trainingDao.getDataOfTypeTraining(typeId, locale)
                        .map {
                            ExerciseItem(
                                id = it.id,
                                title = it.title,
                                state = 1,
                                orderIndex = it.orderIndex,
                                item = List(4) { setIndex ->
                                    ExerciseItemData(
                                        it.id,
                                        index = setIndex,
                                        count = "0",
                                        weight = "0",
                                        prevCount = "0",
                                        prevWeight = "0"
                                    )
                                }
                            )
                        }
                }

            Log.d("ddd1", list.toString())
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getById(id: Int?): Result<TrainingEntity> {
        return try {
            val locale = Locale.current.language

            if (id == null) {
                val typeCategory = typeTrainingDao.getById(null)

                val listData =
                    trainingDao.getExercisesByCategoryIdWithLastData(typeCategory?.id, locale)
                        .map { it.toItem() }

                Result.success(
                    TrainingEntity(
                        id = null,
                        title = "",
                        category = typeCategory,
                        date = LocalDate.now().toString(),
                        items = listData
                    )
                )
            } else {
                val training = trainingDao.getById(id) ?: return Result.failure(
                    IllegalStateException("Training is not found")
                )

                val listData = trainingDao.getExerciseDataByTrainingIdWithPrevData(id, locale)
                    .map { it.toItem() }

                Result.success(
                    TrainingEntity(
                        id = training.id,
                        title = training.name,
                        category = if (training.categoryId != null) TypeTraining(
                            training.categoryId,
                            training.category ?: "not_category"
                        ) else null,
                        date = training.date,
                        items = listData
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertDataTraining(items: List<DataTraining>): Result<Int> {
        return try {
            Result.success(trainingDao.insertDataTraining(items).size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSerialNumOfCategory(typeTraining: Int?): Result<String> {
        return try {
            val result = trainingDao.getSerialNumOfCategory(typeTraining)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun upsertTraining(entity: TrainingEntity): Result<Int> {
        return try {
            Result.success(trainingDao.insertOrUpdate(entity))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Calendar

    override fun getList(start: String, end: String): Flow<List<Training>> {
        return trainingDao.getDataOfMonth(start, end).catch { emit(emptyList()) }
    }

    // Categories

    override suspend fun deleteCategory(it: TypeTraining): Result<Int> {
        return try {
            Result.success(typeTrainingDao.delete(it))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCategoriesList(): Flow<List<TypeTraining>> {
        return typeTrainingDao.getList().catch { emit(emptyList()) }
    }


    // CategoryPage

    override suspend fun getCategoryEntity(id: Int?): Result<CategoryEntity> {
        return try {
            val locale = Locale.current.language
            val category = typeTrainingDao.getById(id) ?: TypeTraining(name = "")
            val items = trainingDao.getDataOfTypeTraining(id, locale)

            Result.success(
                CategoryEntity(
                    category = category,
                    items = items
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertCategory(
        item: TypeTraining,
        exercises: List<DataTypeTrainings>
    ): Result<Int> {
        return try {
            Result.success(typeTrainingDao.createTypeWithExercises(item, exercises))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDataCategory(items: List<DataTypeTrainings>): Result<Int> {
        return try {
            Result.success(typeTrainingDao.insertDataTypeTrainings(items).size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkNameCategoryExists(name: String, id: Int): Result<Boolean> {
        return try {
            Result.success(typeTrainingDao.checkIfNameExists(name, id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}