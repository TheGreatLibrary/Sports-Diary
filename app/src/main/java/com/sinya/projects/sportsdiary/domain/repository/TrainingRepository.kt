package com.sinya.projects.sportsdiary.domain.repository

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.data.database.dao.TrainingsDao
import com.sinya.projects.sportsdiary.data.database.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.data.database.entity.DataTraining
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.data.database.entity.Trainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.domain.model.ExerciseItem
import com.sinya.projects.sportsdiary.domain.model.ExerciseItemData
import com.sinya.projects.sportsdiary.domain.model.Training
import com.sinya.projects.sportsdiary.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.domain.model.toItem
import com.sinya.projects.sportsdiary.ui.features.diagram.ChartPoint
import jakarta.inject.Inject
import java.time.LocalDate

interface TrainingRepository {
    // Statistic
    suspend fun getCountOfTrainings(): Result<Int>
    suspend fun getSummaryWeightOfTrainings(): Result<Float>
    suspend fun getChartList(mode: TypeTime): Result<List<ChartPoint>>

    // Training
    suspend fun getTrainingList(): Result<List<Training>>
    suspend fun deleteTraining(it: Trainings): Result<Int>
    suspend fun getCategoriesList(): Result<List<TypeTraining>>

    // TrainingPage
    suspend fun getById(id: Int?): Result<TrainingEntity>
    suspend fun insertCategory(name: String, exercises: List<DataTypeTrainings>): Result<Long>
    suspend fun insertDataTraining(items: List<DataTraining>): Result<Int>
    suspend fun upsertTraining(entity: TrainingEntity): Result<Int>
    suspend fun getSerialNumOfCategory(typeTraining: Int?): Result<String>
    suspend fun getDataByTypeTraining(typeId: Int): Result<List<ExerciseItem>>

    // Calendar
    suspend fun getList(start: String, end: String): Result<List<Training>>
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

    override suspend fun getTrainingList(): Result<List<Training>> {
        return try {
            val list = trainingDao.getList()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTraining(it: Trainings): Result<Int> {
        return try {
            val result = trainingDao.deleteTraining(it)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoriesList(): Result<List<TypeTraining>> {
        return try {
            val list = typeTrainingDao.getList()
            Result.success(list)
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
                val typeCategory = typeTrainingDao.getById(1) ?: return Result.failure(
                    IllegalStateException("TrainingCategory is not found")
                )

                val listData =
                    trainingDao.getExercisesByCategoryIdWithLastData(typeCategory.id, locale)
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
                        category = TypeTraining(
                            training.categoryId,
                            training.category
                        ),
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

    override suspend fun insertCategory(name: String, exercises: List<DataTypeTrainings>): Result<Long> {
        return try {
            Result.success(typeTrainingDao.createTypeWithExercises(name, exercises))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSerialNumOfCategory(typeTraining: Int?): Result<String> {
        return try {
            Result.success(trainingDao.getSerialNumOfCategory(typeTraining))
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

    override suspend fun getList(start: String, end: String): Result<List<Training>> {
        return try {
            Result.success(trainingDao.getDataOfMonth(start, end))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}