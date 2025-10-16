package com.sinya.projects.sportsdiary.data.database.repository

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.sinya.projects.sportsdiary.data.database.AppDatabase
import com.sinya.projects.sportsdiary.data.database.dao.ExercisesDao
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.presentation.trainingPage.bottomSheetCategory.ExerciseUi
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray


data class ExerciseJson(
    val id: Int,
    val name: String,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val instructions: List<String>
)

interface ExercisesRepository {
    suspend fun getExercisesList() : List<ExerciseUi>
    suspend fun importExercisesFromAssets(fileName: String = "exercises.json", context: Context) : Int
    suspend fun getExerciseTranslations() : List<ExerciseTranslations>
    suspend fun getExerciseById(id: Int) : ExerciseTranslations
}

class ExercisesRepositoryImpl @Inject  constructor(
    private val exercisesDao: ExercisesDao,
) : ExercisesRepository {

    override suspend fun getExercisesList(): List<ExerciseUi> {
        val db = exercisesDao.getExercisesList()
        Log.d("Tag4", db.toString())
        return db + listOf(
            ExerciseUi(1,"Жим лежа", false),
            ExerciseUi(2,"Жим стоя", false),
            ExerciseUi(3,"Жим сидя", false),
            ExerciseUi(4,"Жим в наклоне", false),
        )
    }

    override suspend fun importExercisesFromAssets(fileName: String, context: Context): Int {
        return withContext(Dispatchers.IO) {
            try {
                // Проверяем, есть ли уже данные
                val count = exercisesDao.getCount()
                if (count > 0) {
                    println("⚠ База уже содержит $count упражнений. Импорт пропущен.")
                  //  return@withContext count
                }

                // Читаем JSON из assets
                val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }

                // Парсим JSON
                val jsonArray = JSONArray(jsonString)
                val exercises = mutableListOf<Exercises>()
                val exercisesTranslations = mutableListOf<ExerciseTranslations>()
                val musclesList = mutableSetOf<String>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val exerciseJson = ExerciseJson(
                        id = i + 1,
                        name = jsonObject.optString("name", ""),
                        primaryMuscles = jsonArrayToList(jsonObject.optJSONArray("primaryMuscles")),
                        secondaryMuscles = jsonArrayToList(jsonObject.optJSONArray("secondaryMuscles")),
                        instructions = jsonArrayToList(jsonObject.optJSONArray("instructions"))
                    )

                    exerciseJson.primaryMuscles.forEach {
                        musclesList.add(it)
                    }
                    exerciseJson.secondaryMuscles.forEach {
                        musclesList.add(it)
                    }

//                    if (exerciseJson.name.isNotEmpty()) {
//                        exercises.add(
//                            Exercises(
//                                id = exerciseJson.id,
//                                icon = ""
//                            )
//                        )
//                        exercisesTranslations.add(
//                            ExerciseTranslations(
//                                exerciseId = exerciseJson.id,
//                                language = "en",
//                                name = exerciseJson.name,
//                                description = exerciseJson.secondaryMuscles.joinToString(", ") + exerciseJson.primaryMuscles.joinToString(", "),
//                                rule = exerciseJson.instructions.joinToString("\n\n"),
//                                )
//                        )
//
//                    }
                }

                Log.d("Musc", musclesList.toString())
                // Вставляем все упражнения одним батчем
              //  exercisesDao.insertExercisesData(exercises, exercisesTranslations)

                val insertedCount = exercisesDao.getCount()
                println("✓ Успешно импортировано $insertedCount упражнений")

                insertedCount

            } catch (e: Exception) {
                e.printStackTrace()
                println("❌ Ошибка при импорте: ${e.message}")
                0
            }
        }
    }

    override suspend fun getExerciseTranslations(): List<ExerciseTranslations> {
        return exercisesDao.getExercisesTranslations()
    }

    override suspend fun getExerciseById(id: Int): ExerciseTranslations {
        return exercisesDao.getExerciseById(id)
    }

    private fun jsonArrayToList(jsonArray: JSONArray?): List<String> {
        if (jsonArray == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }


}