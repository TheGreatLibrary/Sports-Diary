package com.sinya.projects.sportsdiary.core.data.dataBase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTraining
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Exercises
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Trainings
import com.sinya.projects.sportsdiary.core.domain.model.ChartDataExercises
import com.sinya.projects.sportsdiary.core.domain.model.ChartDataTrainings
import com.sinya.projects.sportsdiary.core.domain.model.DataTrainings
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseData
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItemWithoutList
import com.sinya.projects.sportsdiary.core.domain.model.Training
import com.sinya.projects.sportsdiary.core.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.core.domain.model.toExerciseData
import com.sinya.projects.sportsdiary.ui.features.map.model.DataExerciseMuscleRaw
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingsDao {

    // Statistic

    @Query("SELECT COUNT(*) FROM trainings")
    suspend fun getCount(): Int

    @Query("""
        SELECT
            count_result AS count, 
            weight_result AS weight,
            mode AS mode,
            CASE
            WHEN d.weight = 1 THEN
                COALESCE(
                    (
                       SELECT dp.value
                        FROM proportions p 
                        JOIN data_proportions dp ON p.id = dp.proportion_id
                        WHERE dp.type_id = 22 AND p.date <= t.date
                        ORDER BY p.date DESC
                        LIMIT 1
                    ),
                    0
                )
            ELSE 0
        END AS weightState,
            strftime('%Y-%m-%d', t.date) AS date
        FROM trainings t JOIN data_training d ON t.id = d.training_id
        WHERE d.state = 1
    """)
    suspend fun getChartList() : List<ChartDataTrainings>

    @Query("""
        SELECT 
            count_result AS count, 
            weight_result AS weight,
            mode AS mode,
           CASE
            WHEN d.weight = 1 THEN
                COALESCE(
                    (
                       SELECT dp.value
                        FROM proportions p 
                        JOIN data_proportions dp ON p.id = dp.proportion_id
                        WHERE dp.type_id = 22 AND p.date <= t.date
                        ORDER BY p.date DESC
                        LIMIT 1
                    ),
                    0
                )
            ELSE 0
        END AS weightState
        FROM trainings t JOIN data_training d ON t.id = d.training_id
        WHERE d.state = 1
    """)
    suspend fun getDataOfTraining() : List<DataTrainings>

    // Training

    @Query("""
        SELECT 
            t.id, 
            t.serial_num as name, 
            t.type_id as categoryId, 
            tt.name as category, 
              COALESCE(
            strftime('%Y-%m-%d', t.date),
            strftime('%Y-%m-%d', 'now','localtime')
        ) AS date
        FROM trainings t LEFT JOIN type_training tt ON t.type_id = tt.id
    """)
    fun getList() : Flow<List<Training>>

    @Delete
    suspend fun deleteTraining(it: Trainings): Int

    // TrainingPage

    @Query("DELETE FROM data_training WHERE training_id = :trainingId")
    suspend fun deleteDataTrainingByTrainingId(trainingId: Int)

    @Transaction
    suspend fun insertOrUpdate(item: TrainingEntity): Int {

        val id = upsertTraining(
            Trainings(
                id = item.id ?: 0,
                typeId = item.category?.id,
                serialNum = item.title.ifEmpty { getSerialNumOfCategory(item.category?.id) }
                    .toIntOrNull() ?: 0,
                date = item.date
            )
        ).toInt().let { id ->
            if (item.id != null && item.id != 0) item.id else id
        }

        deleteDataTrainingByTrainingId(id)

        if (item.items.isNotEmpty()) {
            insertDataTraining(item.items.map {
                DataTraining(
                    trainingId = id,
                    exerciseId = getExerciseId(it.title)?.id ?: 0,
                    countResult = it.toExerciseData().countResult,
                    weightResult = it.toExerciseData().weightResult,
                    mode = it.mode.mode,
                    state = it.state.state,
                    weight = it.weightState.state,
                    orderIndex = it.orderIndex
                )
            })
        }

        return id
    }

    @Upsert
    suspend fun upsertTraining(item: Trainings) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDataTraining(refs: List<DataTraining>): List<Long>

    @Query("""
        SELECT COUNT(*) + 1 
        FROM trainings t 
        WHERE COALESCE(t.type_id, -1) = COALESCE(:categoryId, -1)
    """)
    suspend fun getSerialNumOfCategory(categoryId: Int?) : String

    @Query("""
        SELECT
            id,
            icon,
            is_custom,
            force_id,
            level_id,
            mechanic_id,
            equipment_id,
            category_id
        FROM exercises e 
        JOIN exercise_translations et ON e.id = et.exercise_id
        WHERE name = :name 
        LIMIT 1
    """)
    suspend fun getExerciseId(name: String) : Exercises?

    @Query("""
        SELECT 
            t.id AS id,
            COALESCE(t.serial_num, t.id) AS name,
            t.type_id AS categoryId,
            tt.name AS category,
            COALESCE(
                strftime('%Y-%m-%d', t.date),
                strftime('%Y-%m-%d', 'now','localtime')
            ) AS date
        FROM trainings t LEFT JOIN type_training tt ON t.type_id = tt.id
        WHERE t.id = :id
    """)
    fun getById(id: Int): Flow<Training?>

    @Query("""
      SELECT 
        e.id,
        et.name AS title,
        d.mode AS mode,
        d.weight AS weight,
        d.order_index AS orderIndex
      FROM data_type_trainings d
      JOIN exercises e ON e.id = d.exercise_id
      JOIN exercise_translations et ON e.id = et.exercise_id
      WHERE d.type_id = :id AND et.language = :lang
    """)
    suspend fun getDataOfTypeTraining(id: Int?, lang: String): List<ExerciseItemWithoutList>


    @Query("""
        SELECT
            e.id AS id,
            et.name AS title,
            d.count_result AS countResult,
            d.weight_result AS weightResult,
            COALESCE(prev_d.count_result, '') AS prevCountResult,
            COALESCE(prev_d.weight_result, '') AS prevWeightResult,
            COALESCE(d.state, 1) AS state,
            COALESCE(d.weight, prev_d.weight) AS weight,
            COALESCE(d.mode, prev_d.mode) AS mode,
            d.order_index AS orderIndex
        FROM trainings t
        JOIN data_training d ON d.training_id = t.id
        JOIN exercises e ON e.id = d.exercises_id
        JOIN exercise_translations et ON et.exercise_id = e.id AND et.language = :lang
        LEFT JOIN data_training prev_d ON prev_d.exercises_id = e.id AND prev_d.training_id = (
                SELECT t_prev.id
                FROM trainings t_prev
                WHERE t_prev.type_id = t.type_id AND (t_prev.date < t.date OR (t_prev.date = t.date AND t_prev.serial_num < t.serial_num))
                ORDER BY t_prev.date DESC, t_prev.serial_num DESC
                LIMIT 1
        )
        WHERE t.id = :id
        ORDER BY d.order_index
    """)
    fun getExerciseDataByTrainingIdWithPrevData(id: Int?, lang: String): Flow<List<ExerciseData>>

    @Query("""
       SELECT
            e.id AS id,
            et.name AS title,
            COALESCE(d.count_result, '0/0/0/0') AS countResult,
            COALESCE(d.weight_result, '0/0/0/0') AS weightResult,
            COALESCE(d.count_result, '0/0/0/0') AS prevCountResult,
            COALESCE(d.weight_result, '0/0/0/0') AS prevWeightResult,
            COALESCE(d.state, 1) AS state,
            COALESCE(d.mode, dc.mode) AS mode,
            COALESCE(d.weight, dc.weight) AS weight,
            dc.order_index AS orderIndex
        FROM exercises e
        JOIN data_type_trainings dc ON dc.exercise_id = e.id
        JOIN exercise_translations et ON et.exercise_id = e.id AND et.language = :lang
        LEFT JOIN data_training d
            ON d.exercises_id = e.id
           AND d.training_id = (
                SELECT t_last.id
                FROM trainings t_last
                WHERE t_last.type_id = :categoryId
                ORDER BY t_last.date DESC, t_last.serial_num DESC
                LIMIT 1
           )
        WHERE dc.type_id = :categoryId
        ORDER BY dc.order_index
    """)
    suspend fun getExercisesByCategoryIdWithLastData(categoryId: Int?, lang: String): List<ExerciseData>

    // Calendar

    @Query("""
        SELECT 
            t.id, 
            t.serial_num as name, 
            t.type_id as categoryId, 
            tt.name as category, 
           COALESCE(
            strftime('%Y-%m-%d', t.date),
            strftime('%Y-%m-%d', 'now','localtime')
        ) AS date
        FROM trainings t LEFT JOIN type_training tt ON t.type_id = tt.id
        WHERE date >= :startDate AND date <= :endDate
        ORDER BY date
    """)
    fun getDataOfMonth(startDate: String, endDate: String) : Flow<List<Training>>
    @Query("""
    SELECT
        d.exercises_id AS exerciseId,
        d.count_result AS count,
        d.weight_result AS weight,
        serial_num AS trainingNumber,
        d.mode AS mode,
        CASE
            WHEN d.weight = 1 THEN
                COALESCE(
                    (
                       SELECT dp.value
                        FROM proportions p 
                        JOIN data_proportions dp ON p.id = dp.proportion_id
                        WHERE dp.type_id = 22 AND p.date <= t.date
                        ORDER BY p.date DESC
                        LIMIT 1
                    ),
                    0
                )
            ELSE 0
        END AS weightState,
        strftime('%Y-%m-%d', t.date) AS date,
        COALESCE(tt.name, "") AS categoryName
    FROM trainings t
    JOIN data_training d ON t.id = d.training_id
    LEFT JOIN type_training tt ON t.type_id = tt.id
    WHERE d.exercises_id IN (:list)
    """)
    suspend fun getTrainingsDataList(list: List<Int>) : List<ChartDataExercises>

    @Query("""
        SELECT 
            em.muscle_id AS muscleId,
            name,
            value AS level,
            dt.count_result AS countResult,
            dt.weight_result AS weightResult,
            dt.mode AS mode,
            dt.weight AS weight,
            dt.state AS state
        FROM exercise_muscles em 
        JOIN muscle_translations mt ON em.muscle_id = mt.muscle_id
        JOIN data_training dt ON dt.exercises_id = em.exercise_id
        JOIN trainings t ON t.id = dt.training_id
        WHERE t.date >= :fromDate AND t.date <= :toDate AND language = :language AND dt.state = 1
    """)
    suspend fun getDataExerciseMusclesByDate(fromDate: String, toDate: String, language: String): List<DataExerciseMuscleRaw>
}