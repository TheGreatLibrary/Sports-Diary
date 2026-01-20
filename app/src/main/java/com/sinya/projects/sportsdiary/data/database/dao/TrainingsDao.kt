package com.sinya.projects.sportsdiary.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.sinya.projects.sportsdiary.data.database.entity.DataTraining
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.data.database.entity.Trainings
import com.sinya.projects.sportsdiary.domain.model.ChartDataTrainings
import com.sinya.projects.sportsdiary.domain.model.DataTrainings
import com.sinya.projects.sportsdiary.domain.model.ExerciseData
import com.sinya.projects.sportsdiary.domain.model.ExerciseItemWithoutList
import com.sinya.projects.sportsdiary.domain.model.Training
import com.sinya.projects.sportsdiary.domain.model.TrainingEntity
import com.sinya.projects.sportsdiary.domain.model.toExerciseData

@Dao
interface TrainingsDao {

    // Statistic

    @Query("SELECT COUNT(*) FROM trainings")
    suspend fun getCount(): Int

    @Query("""
        SELECT
            count_result AS count, 
            weight_result AS weight,
            strftime('%Y-%m-%d', t.date) AS date
        FROM trainings t JOIN data_training d ON t.id = d.training_id
    """)
    suspend fun getChartList() : List<ChartDataTrainings>

    @Query("SELECT count_result AS count, weight_result AS weight FROM data_training")
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
        FROM trainings t JOIN type_training tt ON t.type_id = tt.id
    """)
    suspend fun getList() : List<Training>

    @Delete
    suspend fun deleteTraining(it: Trainings): Int

    // TrainingPage

    @Query("DELETE FROM data_training WHERE training_id = :trainingId")
    suspend fun deleteDataTrainingByTrainingId(trainingId: Int)

    @Transaction
    suspend fun insertOrUpdate(item: TrainingEntity): Int {

        val id = upsertTraining(
            Trainings(
                id = item.id?:0,
                typeId = item.category.id,
                serialNum = item.title.ifEmpty { getSerialNumOfCategory(item.category.id) }.toIntOrNull() ?: 0,
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
                    exerciseId = getExerciseId(it.title)?.id?: 0,
                    countResult = it.toExerciseData().countResult,
                    weightResult = it.toExerciseData().weightResult,
                    state = it.state,
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
        FROM trainings tx 
        WHERE tx.type_id = COALESCE(:categoryId, 1)
    """)
    suspend fun getSerialNumOfCategory(categoryId: Int?) : String

    @Query("""
        SELECT * 
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
            COALESCE(t.type_id, 1) AS categoryId,
            COALESCE(tt.name, 'not_category') AS category,
            COALESCE(
                strftime('%Y-%m-%d', t.date),
                strftime('%Y-%m-%d', 'now','localtime')
            ) AS date
        FROM trainings t JOIN type_training tt ON t.type_id = tt.id
        WHERE t.id = :id
    """)
    suspend fun getById(id: Int): Training?

    @Query("""
      SELECT 
        e.id,
        et.name AS title,
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
            d.state,
            prev_d.order_index AS orderIndex
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
        ORDER BY prev_d.order_index
    """)
    suspend fun getExerciseDataByTrainingIdWithPrevData(id: Int?, lang: String): List<ExerciseData>

    @Query("""
       SELECT
            e.id AS id,
            et.name AS title,
            COALESCE(d.count_result, '0/0/0/0') AS countResult,
            COALESCE(d.weight_result, '0/0/0/0') AS weightResult,
            COALESCE(d.count_result, '0/0/0/0') AS prevCountResult,
            COALESCE(d.weight_result, '0/0/0/0') AS prevWeightResult,
            d.state,
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
        FROM trainings t JOIN type_training tt ON t.type_id = tt.id
        WHERE date >= :startDate AND date < :endDate
        ORDER BY date
    """)
    suspend fun getDataOfMonth(startDate: String, endDate: String) : List<Training>
}