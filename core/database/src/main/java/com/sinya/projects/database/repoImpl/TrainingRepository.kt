package com.sinya.projects.database.repoImpl

import android.util.Log
import androidx.core.graphics.toColor
import com.sinya.projects.database.dao.TrainingsDao
import com.sinya.projects.database.dao.TypeTrainingDao
import com.sinya.projects.database.entity.TypeTraining
import com.sinya.projects.database.mapper.toDomain
import com.sinya.projects.database.mapper.toEntity
import com.sinya.projects.domain.enums.TrainingMode
import com.sinya.projects.domain.enums.TypeTime
import com.sinya.projects.domain.enums.Visibility
import com.sinya.projects.domain.enums.WeightState
import com.sinya.projects.domain.model.CategoryEntity
import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.model.ChartPoint
import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.model.DataTrainingEntry
import com.sinya.projects.domain.model.DataTypeTrainingsEntry
import com.sinya.projects.domain.model.ExerciseItem
import com.sinya.projects.domain.model.ExerciseItemData
import com.sinya.projects.domain.model.Training
import com.sinya.projects.domain.model.TrainingEntity
import com.sinya.projects.domain.model.TrainingsEntry
import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.domain.model.toItem
import com.sinya.projects.domain.providers.LanguageProvider
import com.sinya.projects.domain.providers.MapStateProvider
import com.sinya.projects.domain.repository.TrainingRepository
import com.sinya.projects.domain.utils.calculateLoadPercentage
import com.sinya.projects.domain.utils.calculateTonnagePerMuscle
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.Result
import kotlin.collections.distinct
import kotlin.collections.forEach
import kotlin.collections.map

@OptIn(ExperimentalCoroutinesApi::class)
internal class TrainingRepositoryImpl @Inject constructor(
    private val typeTrainingDao: TypeTrainingDao,
    private val trainingDao: TrainingsDao,
    private val languageProvider: LanguageProvider,
    private val mapStateProvider: MapStateProvider
) : TrainingRepository {

    // Statistic

    override suspend fun getCountOfTrainings(): Result<Int> {
        return runCatching { trainingDao.getCount() }
    }

    override suspend fun getSummaryWeightOfTrainings(): Result<Float> {
        return runCatching {
            val data = trainingDao.getDataOfTraining()
            var weight = 0f

            data.forEach {
                val countArr = it.count.split('/')
                val weightArr = it.weight.split('/')

                for (i in countArr.indices) {
                    weight += countArr[i].toFloat() * (weightArr[i].toFloat() + it.weightState) /
                            if (TrainingMode.getMode(it.mode) == TrainingMode.TIME) 4f else 1f
                }
            }

            val result = weight / 1000

            String.format(java.util.Locale.ENGLISH, "%.1f", result).toFloat()
        }
    }

    override suspend fun getPrevTrainingSummaryWeight(
        categoryId: Int?,
        trainingId: Int
    ): Result<Float> {
        return runCatching {
            val prevId = trainingDao.getLastPrevId(categoryId, trainingId)
            val prevData =
                if (prevId != null) trainingDao.getPrevTrainingSummaryWeight(prevId) else emptyList()
            var weight = 0f

            prevData.forEach {
                val countArr = it.count.split('/')
                val weightArr = it.weight.split('/')

                for (i in countArr.indices) {
                    weight += countArr[i].toFloat() * (weightArr[i].toFloat() + it.weightState) /
                            if (TrainingMode.getMode(it.mode) == TrainingMode.TIME) 4f else 1f
                }
            }

            val result = weight / 1000

            String.format(java.util.Locale.ENGLISH, "%.1f", result).toFloat()
        }
    }

    override suspend fun getChartList(mode: TypeTime): Result<List<ChartLine>> {
        return runCatching {
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
                        totalWeight += countArr[i].toFloat() * (weightArr[i].toFloat() + item.weightState) / if (TrainingMode.getMode(
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


            listOf(
                ChartLine(
                    id = result.first().id,
                    points = result,
                    lineColor = 0xFFFFFFF.toColor()

                )
            )
        }
    }

    // Training

    override fun getTrainingList(): Flow<List<Training>> {
        return trainingDao.getList().catch { emit(emptyList()) }
    }

    override suspend fun deleteTrainings(items: List<TrainingsEntry>): Result<Int> {
        return runCatching { trainingDao.deleteTrainings(items.map { it.toEntity() }) }
    }

    // TrainingPage

    override suspend fun getDataByTypeTraining(typeId: Int): Result<List<ExerciseItem>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            val lastTrainingItems = trainingDao.getExercisesByCategoryIdWithLastData(typeId, lang)

            if (lastTrainingItems.isNotEmpty()) lastTrainingItems.map { it.toItem() }
            else {
                trainingDao.getDataOfTypeTraining(typeId, lang)
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
        }
    }

    override fun observeTrainingEntityById(id: Int): Flow<TrainingEntity> {
        return languageProvider.currentLanguage.flatMapLatest { language ->
            combine(
                trainingDao.getById(id),
                trainingDao.getExerciseDataByTrainingIdWithPrevData(id, language)
            ) { training, items ->
                when (training) {
                    null -> throw IllegalStateException("Training with id=$id not found")
                    else -> TrainingEntity(
                        id = training.id,
                        title = training.name,
                        category = if (training.categoryId != null) TypeTrainingEntry(
                            id = training.categoryId ?: 0,
                            name = training.category ?: "not_category"
                        ) else null,
                        date = training.date,
                        items = items.map { it.toItem() }
                    )
                }
            }
        }
    }

    override fun getNewTraining(date: String?): Result<TrainingEntity> {
        return runCatching {
            TrainingEntity(
                id = null,
                title = "",
                category = null,
                date = date ?: LocalDate.now().toString(),
                items = emptyList()
            )
        }
    }

    override suspend fun insertDataTraining(items: List<DataTrainingEntry>): Result<Int> {
        return runCatching { trainingDao.insertDataTraining(items.map { it.toEntity() }).size }
    }

    override suspend fun getSerialNumOfCategory(typeTraining: Int?): Result<String> {
        return runCatching { trainingDao.getSerialNumOfCategory(typeTraining) }
    }

    override suspend fun upsertTraining(entity: TrainingEntity): Result<Int> {
        return runCatching { trainingDao.insertOrUpdate(entity) }
    }

    // Calendar

    override fun getList(start: String, end: String): Flow<List<Training>> {
        return trainingDao.getDataOfMonth(start, end).catch { emit(emptyList()) }
    }

    // Categories

    override suspend fun deleteCategory(it: List<TypeTrainingEntry>): Result<Int> {
        return runCatching { typeTrainingDao.delete(it.map { it.toEntity() }) }
    }

    override fun observeCategoriesList(): Flow<List<TypeTrainingEntry>> {
        return typeTrainingDao.getList().map { item ->
            item.map {
                it.toDomain()
            }
        }.catch { emit(emptyList()) }
    }

    // CategoryPage

    override suspend fun getCategoryEntity(id: Int?): Result<CategoryEntity> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()
            val category = typeTrainingDao.getById(id) ?: TypeTraining(name = "")
            val items = trainingDao.getDataOfTypeTraining(id, lang)

            CategoryEntity(
                category = category.toDomain(),
                items = items
            )
        }
    }

    override suspend fun insertCategory(
        item: TypeTrainingEntry,
        exercises: List<DataTypeTrainingsEntry>
    ): Result<Int> {
        return runCatching {
            typeTrainingDao.createTypeWithExercises(
                item.toEntity(),
                exercises.map { it.toEntity() })
        }
    }

    override suspend fun updateDataCategory(items: List<DataTypeTrainingsEntry>): Result<Int> {
        return runCatching {
            typeTrainingDao.insertDataTypeTrainings(items.map { it.toEntity() })
            items.first().typeId
        }
    }

    override suspend fun checkNameCategoryExists(name: String, id: Int): Result<Boolean> {
        return runCatching { typeTrainingDao.checkIfNameExists(name, id) }
    }

    override suspend fun getExercisesData(list: List<Int>): Result<List<ChartLine>> {
        return runCatching {
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
                                totalWeight += countArr[i].toFloat() * (weightArr[i].toFloat() + it.weightState) / if (TrainingMode.getMode(
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
                        lineColor = 0xFFFFFFF.toColor()

                    )
                }
        }
    }

    override suspend fun getDataExerciseMuscles(
        from: String,
        to: String
    ): Result<List<DataExerciseMuscles>> = coroutineScope {
        runCatching {
            val lang = languageProvider.currentLanguage.first()
            // Читаем твой новый флаг из MapStateProvider (подставь имя своего поля/Flow)
            val isRollingSevenDays = mapStateProvider.currentMapState.first()

            val targetDate = LocalDate.parse(to)

            // 1. Вычисляем интервалы в зависимости от флага MapStateProvider
            val (currentFromDate, currentToDate) = if (isRollingSevenDays) {
                // Режим TRUE: скользящие 7 дней [today - 6, today]
                targetDate.minusDays(6) to targetDate
            } else {
                // Режим FALSE: календарная неделя с Понедельника по Воскресенье
                val monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val sunday = targetDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                monday to sunday
            }

            // Предыдущая неделя — всегда ровно на 7 дней раньше выбранного интервала:
            // Для скользящих: [today - 13, today - 7]
            // Для календарной: [прошлый ПН, прошлое ВС]
            val previousFromDate = currentFromDate.minusWeeks(1)
            val previousToDate = currentToDate.minusWeeks(1)

            // 2. Параллельный запуск запросов в БД через async
            val currentRawDataDeferred = async {
                trainingDao.getDataExerciseMusclesByDate(
                    currentFromDate.toString(),
                    currentToDate.toString(),
                    lang
                )
            }
            val previousRawDataDeferred = async {
                trainingDao.getDataExerciseMusclesByDate(
                    previousFromDate.toString(),
                    previousToDate.toString(),
                    lang
                )
            }

            val currentRawData = currentRawDataDeferred.await()
            val previousRawData = previousRawDataDeferred.await()

            // 3. Считаем суммарный тоннаж для каждой мышцы за оба периода
            val currentTonnageMap = calculateTonnagePerMuscle(currentRawData)
            val previousTonnageMap = calculateTonnagePerMuscle(previousRawData)
            // Находим маппинг ID -> Name и ID -> Level из свежих данных (или старых, если в свежих нет)

            val rawItemsMap = (previousRawData + currentRawData).associateBy { it.muscleId }

            rawItemsMap.values.map { rawItem ->
                val currentTonnage = currentTonnageMap[rawItem.muscleId] ?: 0f
                val previousTonnage = previousTonnageMap[rawItem.muscleId] ?: 0f

                val calculatedValue = calculateLoadPercentage(
                    currentTonnage = currentTonnage,
                    previousTonnage = previousTonnage
                )

                DataExerciseMuscles(
                    res = 0,
                    value = calculatedValue,
                    level = rawItem.level,
                    name = rawItem.name,
                    muscleId = rawItem.muscleId,
                    centerX = 0f,
                    centerY = 0f,
                    radiusScale = 0f
                )
            }
        }
    }

    override suspend fun getMuscleDataById(
        id: Int,
        categoryId: Int?
    ): Result<List<DataExerciseMuscles>> {
        return runCatching {
            val lang = languageProvider.currentLanguage.first()

            val prevId = trainingDao.getLastPrevId(categoryId, id)

            val prevData = if (prevId != null) trainingDao.getDataExerciseMusclesById(
                prevId,
                lang
            ) else emptyList()
            val currentData = trainingDao.getDataExerciseMusclesById(id, lang)

            val currentTonnageMap = calculateTonnagePerMuscle(currentData)
            val previousTonnageMap = calculateTonnagePerMuscle(prevData)

            val allMuscleIds = (currentData + prevData).map { it.muscleId }.distinct()
            val rawItemsMap = (prevData + currentData).associateBy { it.muscleId }

            allMuscleIds.map { muscleId ->
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
        }
    }
}