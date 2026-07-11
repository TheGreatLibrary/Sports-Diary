package com.sinya.projects.sportsdiary.core.domain.repository

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TrainingsDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTraining
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Trainings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.domain.enums.TrainingMode
import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.core.domain.enums.Visibility
import com.sinya.projects.sportsdiary.core.domain.enums.WeightState
import com.sinya.projects.sportsdiary.core.domain.model.CategoryEntity
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItem
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItemData
import com.sinya.projects.sportsdiary.core.domain.model.Training
import com.sinya.projects.sportsdiary.core.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.core.domain.model.toItem
import com.sinya.projects.sportsdiary.ui.features.map.model.DataExerciseMuscleRaw
import com.sinya.projects.sportsdiary.ui.features.map.model.DataExerciseMuscles
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartPoint
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

interface TrainingRepository {
    // Statistic
    suspend fun getCountOfTrainings(): Result<Int>
    suspend fun getSummaryWeightOfTrainings(): Result<Float>
    suspend fun getChartList(mode: TypeTime): Result<List<ChartLine>>

    // Training
    fun getTrainingList(): Flow<List<Training>>
    suspend fun deleteTraining(it: Trainings): Result<Int>

    // TrainingPage
    fun observeTrainingEntityById(
        id: Int,
        locale: String = Locale.current.language
    ): Flow<TrainingEntity>

    fun getNewTraining(date: String?): Result<TrainingEntity>


    suspend fun insertDataTraining(items: List<DataTraining>): Result<Int>
    suspend fun upsertTraining(entity: TrainingEntity): Result<Int>
    suspend fun getSerialNumOfCategory(typeTraining: Int?): Result<String>
    suspend fun getDataByTypeTraining(typeId: Int): Result<List<ExerciseItem>>

    // Calendar
    fun getList(start: String, end: String): Flow<List<Training>>

    // Categories
    suspend fun deleteCategory(it: TypeTraining): Result<Int>
    fun observeCategoriesList(): Flow<List<TypeTraining>>

    // CategoryPage

    suspend fun getCategoryEntity(id: Int?): Result<CategoryEntity>
    suspend fun insertCategory(item: TypeTraining, exercises: List<DataTypeTrainings>): Result<Int>
    suspend fun updateDataCategory(items: List<DataTypeTrainings>): Result<Int>
    suspend fun checkNameCategoryExists(name: String, id: Int): Result<Boolean>
    suspend fun getExercisesData(list: List<Int>): Result<List<ChartLine>>
    suspend fun getDataExerciseMuscles(from: String, to: String, language: String = Locale.current.language): Result<List<DataExerciseMuscles>>
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
                    weight += countArr[i].toInt() * (weightArr[i].toFloat() + it.weightState) /
                        if (TrainingMode.getMode(it.mode) == TrainingMode.TIME) 4f else 1f
                }
            }

            val result = weight / 1000

            val roundedResult = String.format(java.util.Locale.ENGLISH, "%.1f", result).toFloat()

            Result.success(roundedResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChartList(mode: TypeTime): Result<List<ChartLine>> {
        return try {
            val raw = trainingDao.getChartList().ifEmpty { return Result.success(emptyList()) }

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
                        totalWeight += countArr[i].toInt() * (weightArr[i].toFloat() + item.weightState) / if (TrainingMode.getMode(
                                item.mode
                            ) == TrainingMode.TIME
                        ) 4f else 1f
                    }
                }

                totalWeight /= 1000f

                val label = when (mode) {
                    TypeTime.DAYS -> LocalDate.parse(key).toString()
                    TypeTime.MONTHS -> LocalDate.parse("$key-01").toString()
                    TypeTime.YEARS -> LocalDate.parse("$key-01-01").toString()
                }

                ChartPoint(xDate = label, yValue = totalWeight)
            }.sortedBy { it.xDate }

            Result.success(
                listOf(
                    ChartLine(
                        id = result.first().id,
                        points = result,
                        lineColor = Color.Unspecified
                    )
                )
            )
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
                                state = Visibility.VISIBLE,
                                weightState = WeightState.NOT_MATTER,
                                mode = TrainingMode.getMode(it.mode),
                                orderIndex = it.orderIndex,
                                items = List(4) { setIndex ->
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

    override fun observeTrainingEntityById(id: Int, locale: String): Flow<TrainingEntity> {
        return combine(
            trainingDao.getById(id),
            trainingDao.getExerciseDataByTrainingIdWithPrevData(id, locale)
        ) { training, items ->
            when(training) {
                null -> throw IllegalStateException("Training with id=$id not found")
                else -> TrainingEntity(
                    id = training.id,
                    title = training.name,
                    category = if (training.categoryId != null) TypeTraining(
                        id = training.categoryId,
                        name = training.category ?: "not_category"
                    ) else null,
                    date = training.date,
                    items = items.map { it.toItem()}
                )
            }
        }
    }

    override fun getNewTraining(date: String?): Result<TrainingEntity> {
        return try {
            Result.success(
                TrainingEntity(
                    id = null,
                    title = "",
                    category = null,
                    date = date ?: LocalDate.now().toString(),
                    items = emptyList()
                )
            )
        }
        catch (e: Exception) {
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

    override fun observeCategoriesList(): Flow<List<TypeTraining>> {
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
            val result = typeTrainingDao.insertDataTypeTrainings(items)

            Result.success(items.first().typeId ?: result.size)
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

    override suspend fun getExercisesData(list: List<Int>): Result<List<ChartLine>> {
        return try {
            val raw =
                trainingDao.getTrainingsDataList(list)
                    .groupBy { it.exerciseId }
                    .map { it ->
                        ChartLine(
                            id = it.key,
                            points = it.value.map {
                                var totalWeight = 0f

                                val countArr = it.count.split('/')
                                val weightArr = it.weight.split('/')

                                for (i in countArr.indices) {
                                    totalWeight += countArr[i].toInt() * (weightArr[i].toFloat() + it.weightState) / if (TrainingMode.getMode(
                                            it.mode
                                        ) == TrainingMode.TIME
                                    ) 4f else 1f
                                }

                                totalWeight /= 1000f


                                ChartPoint(
                                    id = it.exerciseId,
                                    xDate = "${it.categoryName} №${it.trainingNumber}",
                                    yValue = totalWeight,
                                    date = it.date
                                )
                            }
                                .sortedBy { it.date },
                            lineColor = Color.Unspecified
                        )
                    }
            Result.success(raw)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDataExerciseMuscles(
        from: String, // Передаем текущую дату (например, LocalDate.now().toString())
        to: String,   // В данной логике эти параметры можно вычислить внутри или переопределить
        language: String
    ): Result<List<DataExerciseMuscles>> = coroutineScope {
        try {
            // 1. Расчет чистых, непересекающихся интервалов по 7 дней
            val today = LocalDate.parse(to)

            // Текущая неделя (последние 7 дней, включая сегодня): [today - 6 дней, today]
            val currentFrom = today.minusDays(6).toString()
            val currentTo = today.toString()

            // Предыдущая неделя (7 дней до этого): [today - 13 дней, today - 7 дней]
            val previousFrom = today.minusDays(13).toString()
            val previousTo = today.minusDays(7).toString()

            // 2. Параллельный запуск запросов в БД через async
            val currentRawDataDeferred = async {
                trainingDao.getDataExerciseMusclesByDate(currentFrom, currentTo, language)
            }
            val previousRawDataDeferred = async {
                trainingDao.getDataExerciseMusclesByDate(previousFrom, previousTo, language)
            }

            // Ожидаем завершения обоих запросов
            val currentRawData = currentRawDataDeferred.await()
            val previousRawData = previousRawDataDeferred.await()
            Log.d("Body1",currentRawData.toString())
            Log.d("Body2",previousRawData.toString())

            // 3. Считаем суммарный тоннаж для каждой мышцы за оба периода
            val currentTonnageMap = calculateTonnagePerMuscle(currentRawData)
            val previousTonnageMap = calculateTonnagePerMuscle(previousRawData)

            // 4. Объединяем данные и рассчитываем итоговый Float (%) нагрузки
            // Берем все уникальные мышцы, которые встречались хотя бы в одном из периодов
            val allMuscleIds = (currentRawData + previousRawData).map { it.muscleId }.distinct()

            // Находим маппинг ID -> Name и ID -> Level из свежих данных (или старых, если в свежих нет)
            val rawItemsMap = (previousRawData + currentRawData).associateBy { it.muscleId }

            val processedMuscles = allMuscleIds.map { muscleId ->
                val rawItem = rawItemsMap[muscleId]!!

                val currentTonnage = currentTonnageMap[muscleId] ?: 0f
                val previousTonnage = previousTonnageMap[muscleId] ?: 0f

                // Вычисляем процент нагрузки для тепловой карты (Float от 0.0f до 1.0f)
                val calculatedValue = calculateLoadPercentage(
                    currentTonnage = currentTonnage,
                    previousTonnage = previousTonnage
                )

                DataExerciseMuscles(
                    res = 0, // или откуда у тебя берется ссылка на drawable (например, маппинг ID в R.drawable)
                    value = calculatedValue,
                    level = rawItem.level,  // Уровень вовлечения (0, 1, 2) из БД
                    name = rawItem.name,
                    muscleId = rawItem.muscleId,
                    centerX = 0f,
                    centerY = 0f,
                    radiusScale = 0f
                )
            }

            Result.success(processedMuscles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Вспомогательная функция для расчета тоннажа, сгруппированная по ID мышцы
     */
    private fun calculateTonnagePerMuscle(rawList: List<DataExerciseMuscleRaw>): Map<Int, Float> {
        return rawList.groupBy { it.muscleId }.mapValues { (_, entries) ->
            var totalWeight = 0f

            entries.forEach { item ->
                val countArr = item.countResult.split('/')
                val weightArr = item.weightResult.split('/')

                for (i in countArr.indices) {
                    val count = countArr.getOrNull(i)?.toIntOrNull() ?: 0
                    val weight = weightArr.getOrNull(i)?.toFloatOrNull() ?: 0f

                    totalWeight += count * (weight + item.weight) / if (TrainingMode.getMode(item.mode) == TrainingMode.TIME) 4f else 1f
                }
            }
            totalWeight / 1000f
        }
    }

    private fun calculateLoadPercentage(currentTonnage: Float, previousTonnage: Float): Float {
        if (currentTonnage == 0f) return 0.0f
        if (previousTonnage == 0f) return 1.0f
        val ratio = currentTonnage / previousTonnage
        return ratio.coerceIn(0.0f, 1.0f)
    }
}