package com.sinya.projects.sportsdiary.data.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sinya.projects.sportsdiary.data.database.entity.DataTraining
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.data.database.entity.Trainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.model.ChartDataTrainings
import com.sinya.projects.sportsdiary.domain.model.DataTrainings
import com.sinya.projects.sportsdiary.presentation.trainingPage.ExerciseItemWithoutList
import com.sinya.projects.sportsdiary.presentation.trainingPage.ExerciseRow
import com.sinya.projects.sportsdiary.presentation.trainingPage.TrainingEntity
import com.sinya.projects.sportsdiary.domain.model.Training

@Dao
interface TrainingsDao {

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

    @Query("""
      SELECT 
        e.id,
        et.name AS title,
        d.count_result,
        d.weight_result
      FROM data_training d
      JOIN exercises e ON e.id = d.exercises_id
      JOIN exercise_translations et ON e.id = et.exercise_id
      JOIN trainings t ON t.id = d.training_id
      WHERE t.type_id = :categoryId 
        AND et.language = :lang
        AND t.id = (
          SELECT id FROM trainings 
          WHERE type_id = :categoryId 
          ORDER BY date DESC, serial_num DESC 
          LIMIT 1
        )
      ORDER BY e.id
    """)
    suspend fun getLastTrainingExercises(categoryId: Int, lang: String): List<ExerciseRow>

    @Query("""
      SELECT 
        e.id,
        et.name AS title,
        d.count_result,
        d.weight_result
      FROM data_training d
      JOIN exercises e ON e.id = d.exercises_id
      JOIN exercise_translations et ON e.id = et.exercise_id
      WHERE d.training_id = :id AND et.language = :lang
      ORDER BY e.id
    """)
    suspend fun getExerciseRows(id: Int?, lang: String): List<ExerciseRow>



    @Query("""
      WITH picked AS (SELECT * FROM trainings WHERE id = :id)
      SELECT
        COALESCE(p.id, (SELECT COALESCE(MAX(t2.id),0)+1 FROM trainings t2)) AS id,
        CAST(
          COALESCE(
            p.serial_num,
            (SELECT COUNT(*) + 1 FROM trainings tx WHERE tx.type_id = COALESCE(p.type_id, 1))
          ) AS TEXT
        ) AS name,
        COALESCE(p.type_id, 1) AS categoryId,
        COALESCE(tt.name, 'not_category') AS category,
        COALESCE(
            strftime('%Y-%m-%d', p.date),
            strftime('%Y-%m-%d', 'now','localtime')
        ) AS date
      FROM (SELECT 1) AS dummy
      LEFT JOIN picked p ON 1=1
      LEFT JOIN type_training tt ON tt.id = COALESCE(p.type_id, 1)
      LIMIT 1
    """)
    suspend fun getById(id: Int?): Training

    @Query("""
        SELECT COUNT(*) + 1 
        FROM trainings tx 
        WHERE tx.type_id = COALESCE(:categoryId, 1)
    """)
    suspend fun getSerialNumOfCategory(categoryId: Int?) : String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(item: Trainings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataTraining(refs: List<DataTraining>)

    @Transaction
    suspend fun insertOrUpdate(item: TrainingEntity) {
        Log.d("Tag", "${item}")

        val training = Trainings(
            id = item.id?:0,
            typeId = item.category.id,
            serialNum = item.title.toInt(),
            date = item.date
        )
        if (getById(item.id).id==item.id) {
            Log.d("Tag", "${item.id}")
            insertTraining(training)
            if (item.items.isNotEmpty()) {
                insertDataTraining(item.items.map {
                    DataTraining(
                        trainingId = item.id,
                        exerciseId = getExerciseId(it.title)?.id?: 0,
                        countResult = it.countList.joinToString(separator = "/"),
                        weightResult = it.weightList.joinToString(separator = "/"),
                    )
                })
            }
        }
        else {
            updateTraining(training)
            if (item.items.isNotEmpty()) {
                insertDataTraining(item.items.map {
                    DataTraining(
                        trainingId = item.id?:0,
                        exerciseId = getExerciseId(it.title)?.id?: 0,
                        countResult = it.countList.joinToString(separator = "/"),
                        weightResult = it.weightList.joinToString(separator = "/"),
                    )
                })
            }
        }
    }

    @Update
    suspend fun updateTraining(item: Trainings)

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

    @Query("SELECT * FROM type_training WHERE name = :name LIMIT 1")
    suspend fun getCategoryId(name: String?) : TypeTraining?

    @Query("""
        SELECT * 
        FROM exercises e 
        JOIN exercise_translations et ON e.id = et.exercise_id
        WHERE name = :name 
        LIMIT 1
    """)
    suspend fun getExerciseId(name: String) : Exercises?


    @Query("SELECT COUNT(*) FROM trainings")
    suspend fun getCount(): Int

    @Query("SELECT count_result AS count, weight_result AS weight FROM data_training")
    suspend fun getDataOfTraining() : List<DataTrainings>

    @Query("""
        SELECT
            count_result AS count, 
            weight_result AS weight,
            strftime('%Y-%m-%d', t.date) AS date
        FROM trainings t JOIN data_training d ON t.id = d.training_id
    """)
    suspend fun getChartList() : List<ChartDataTrainings>

    @Query("""
      SELECT 
        e.id,
        et.name AS title,
        'кг' AS unitMeasure
      FROM data_type_trainings d
      JOIN exercises e ON e.id = d.exercise_id
      JOIN exercise_translations et ON e.id = et.exercise_id
      WHERE d.type_id = :id AND et.language = :lang
      ORDER BY e.id
    """)
    suspend fun getDataOfTypeTraining(id: Int, lang: String): List<ExerciseItemWithoutList>

    @Delete
    suspend fun deleteTraining(it: Trainings): Int




}