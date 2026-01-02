package com.sinya.projects.sportsdiary.domain.repository

import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.data.database.dao.TrainingsDao
import com.sinya.projects.sportsdiary.data.database.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.data.database.entity.DataTraining
import com.sinya.projects.sportsdiary.data.database.entity.Trainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.presentation.trainingPage.ExerciseItem
import com.sinya.projects.sportsdiary.presentation.trainings.Training
import com.sinya.projects.sportsdiary.presentation.trainingPage.TrainingEntity
import com.sinya.projects.sportsdiary.presentation.trainingPage.toItem
import com.sinya.projects.sportsdiary.ui.features.diagram.ChartPoint
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate


interface TrainingRepository {
    suspend fun categoriesList(): List<TypeTraining>
    suspend fun insertCategory(name: String, exercises: List<Int>)
    suspend fun getSerialNumOfCategory(typeTraining: Int?): String

    suspend fun getList(start: String, end: String): Result<List<Training>>
    suspend fun trainingList(): List<Training>
    suspend fun insertTraining(entity: TrainingEntity)
    suspend fun updateTraining(entity: TrainingEntity)
    suspend fun delete(it: Trainings)
    suspend fun getById(id: Int?): TrainingEntity
    suspend fun insertDataTraining(items: List<DataTraining>)

    suspend fun getCountOfTrainings(): Result<Int>
    suspend fun getSummaryWeightOfTrainings(): Result<Float>
    suspend fun getChartList(mode: TypeTime): Result<List<ChartPoint>>

    suspend fun getDataByTypeTraining(id: Int): List<ExerciseItem>
}


class TrainingRepositoryImpl @Inject constructor(
    private val typeTrainingDao: TypeTrainingDao,
    private val trainingDao: TrainingsDao
) : TrainingRepository {

    override suspend fun getById(id: Int?): TrainingEntity {
        val locale = Locale.current.language
        val training = trainingDao.getById(id)
        val listData = if (id == null) {
            trainingDao.getLastTrainingExercises(training.categoryId, locale)
                .map { it.toItem() }
        } else {
            trainingDao.getExerciseRows(id, locale)
                .map { it.toItem() }
        }

        return TrainingEntity(
            id = training.id,
            title = training.name,
            category = TypeTraining(
                training.categoryId,
                training.category
            ),
            date = training.date ?: "",
            items = listData
        )
    }

    override suspend fun insertDataTraining(items: List<DataTraining>) {
        withContext(Dispatchers.IO) {
            items.chunked(100).forEach { batch ->
                trainingDao.insertDataTraining(batch)
                delay(10) // Теперь delay доступен
            }
        }
    }


    override suspend fun insertTraining(entity: TrainingEntity) {
        return trainingDao.insertOrUpdate(entity)
    }

    override suspend fun updateTraining(entity: TrainingEntity) {

    }

    override suspend fun delete(it: Trainings) {
        trainingDao.deleteTraining(it)
    }

    override suspend fun trainingList(): List<Training> {
        val db = trainingDao.getList()
        return db
    }


    override suspend fun categoriesList(): List<TypeTraining> {
        val db = typeTrainingDao.getList()
        return db
    }

    override suspend fun insertCategory(name: String, exercises: List<Int>) {
        return typeTrainingDao.createTypeWithExercises(name, exercises)
    }

    override suspend fun getSerialNumOfCategory(typeTraining: Int?): String {
        return trainingDao.getSerialNumOfCategory(typeTraining)
    }

    override suspend fun getList(start: String, end: String): Result<List<Training>> {
        return try {
            Result.success(trainingDao.getDataOfMonth(start, end))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // StatisticScreen

    override suspend fun getCountOfTrainings(): Result<Int> {
        return try {
            val count = trainingDao.getCount()
            Result.success(count)
        }
        catch (e: Exception) {
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

            Result.success(result)
        }
        catch (e: Exception) {
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
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }



    override suspend fun getDataByTypeTraining(id: Int): List<ExerciseItem> {
        val locale = Locale.current.language
        val lastTrainingItems = trainingDao.getLastTrainingExercises(id, locale)

        return if (lastTrainingItems.isNotEmpty()) {
            lastTrainingItems.map { it.toItem() }
        } else {
            trainingDao.getDataOfTypeTraining(id, locale).map {
                ExerciseItem(
                    id = it.id,
                    title = it.title,
                    countList = listOf("0", "0", "0", "0"),
                    weightList = listOf("0", "0", "0", "0"),
                )
            }
        }
    }

}