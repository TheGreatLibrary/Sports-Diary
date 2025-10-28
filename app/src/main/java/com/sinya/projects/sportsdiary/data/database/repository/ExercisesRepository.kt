package com.sinya.projects.sportsdiary.data.database.repository

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.intl.Locale

import com.sinya.projects.sportsdiary.data.database.dao.ExercisesDao
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseMuscles
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

data class ExerciseMusclesData(
    val value: Int,
    val muscleId: Int,
    val exerciseId: Int,
    val language: String,
    val name: String
)

interface ExercisesRepository {
    suspend fun getExercisesList(): List<ExerciseUi>
    suspend fun importExercisesFromAssets(
        fileName: String = "exercise_j.json",
        context: Context
    ): Int

    suspend fun getExerciseTranslations(language: String): List<ExerciseTranslations>
    suspend fun getExerciseById(id: Int, language: String): ExerciseTranslations
    suspend fun getExerciseMusclesById(id: Int, language: String): List<ExerciseMusclesData>
}

class ExercisesRepositoryImpl @Inject constructor(
    private val exercisesDao: ExercisesDao,
) : ExercisesRepository {

    override suspend fun getExercisesList(): List<ExerciseUi> {
        val db = exercisesDao.getExercisesList(Locale.current.language)
        return db
    }

    override suspend fun importExercisesFromAssets(fileName: String, context: Context): Int {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ExerciseImport", "Начало импорта упражнений...")

                val count = exercisesDao.getCount()
                if (count > 0) {
                    Log.w("ExerciseImport", "База уже содержит $count упражнений. Импорт пропущен.")
                    return@withContext count
                }

                // Читаем JSON из assets
                Log.d("ExerciseImport", "Чтение JSON из assets...")
                val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
                Log.d("ExerciseImport", "JSON успешно прочитан, размер: ${jsonString.length} символов")

                // Парсим JSON
                Log.d("ExerciseImport", "Парсинг JSON...")
                val jsonArray = JSONArray(jsonString)
                Log.d("ExerciseImport", "Найдено упражнений в JSON: ${jsonArray.length()}")

                val exercises = mutableListOf<Exercises>()
                val exercisesTranslations = mutableListOf<ExerciseTranslations>()
                val exerciseMuscles = mutableListOf<ExerciseMuscles>()

                // Множества для метаданных
                val forceSet = mutableSetOf<String>()
                val levelSet = mutableSetOf<String>()
                val mechanicSet = mutableSetOf<String>()
                val equipmentSet = mutableSetOf<String>()
                val categorySet = mutableSetOf<String>()

                // Обрабатываем упражнения
                for (i in 0 until jsonArray.length()) {
                    try {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val exerciseId = i + 1

                        val force = jsonObject.optString("force", "")
                        val level = jsonObject.optString("level", "")
                        val mechanic = jsonObject.optString("mechanic", "")
                        val equipment = jsonObject.optString("equipment", "")
                        val category = jsonObject.optString("category", "")

                        // Собираем уникальные значения
                        if (force.isNotEmpty()) forceSet.add(force)
                        if (level.isNotEmpty()) levelSet.add(level)
                        if (mechanic.isNotEmpty()) mechanicSet.add(mechanic)
                        if (equipment.isNotEmpty()) equipmentSet.add(equipment)
                        if (category.isNotEmpty()) categorySet.add(category)

                        val primaryMuscles = jsonArrayToIntList(jsonObject.optJSONArray("primary_muscles"))
                        val secondaryMuscles = jsonArrayToIntList(jsonObject.optJSONArray("secondary_muscles"))
                        val translations = jsonObject.optJSONObject("translations")

                        exercises.add(
                            Exercises(
                                id = exerciseId,
                                icon = ""
                            )
                        )

                        // Добавляем связи с мышцами
                        primaryMuscles.forEach { muscleId ->
                            exerciseMuscles.add(
                                ExerciseMuscles(
                                    muscleId = muscleId,
                                    exerciseId = exerciseId,
                                    value = 2 // primary
                                )
                            )
                        }

                        secondaryMuscles.forEach { muscleId ->
                            exerciseMuscles.add(
                                ExerciseMuscles(
                                    muscleId = muscleId,
                                    exerciseId = exerciseId,
                                    value = 1 // secondary
                                )
                            )
                        }

                        // Английский перевод
                        translations?.optJSONObject("en")?.let { enTranslation ->
                            exercisesTranslations.add(
                                ExerciseTranslations(
                                    exerciseId = exerciseId,
                                    language = "en",
                                    name = enTranslation.optString("name", ""),
                                    description = enTranslation.optString("description", ""),
                                    rule = jsonArrayToList(
                                        enTranslation.optJSONArray("instructions")
                                    ).joinToString("\n\n"),
                                    force = translateForce(force, "en"),
                                    level = translateLevel(level, "en"),
                                    mechanic = translateMechanic(mechanic, "en"),
                                    equipment = translateEquipment(equipment, "en"),
                                    category = translateCategory(category, "en")
                                )
                            )
                        }

                        // Русский перевод
                        translations?.optJSONObject("ru")?.let { ruTranslation ->
                            exercisesTranslations.add(
                                ExerciseTranslations(
                                    exerciseId = exerciseId,
                                    language = "ru",
                                    name = ruTranslation.optString("name", ""),
                                    description = ruTranslation.optString("description", ""),
                                    rule = jsonArrayToList(
                                        ruTranslation.optJSONArray("instructions")
                                    ).joinToString("\n\n"),
                                    force = translateForce(force, "ru"),
                                    level = translateLevel(level, "ru"),
                                    mechanic = translateMechanic(mechanic, "ru"),
                                    equipment = translateEquipment(equipment, "ru"),
                                    category = translateCategory(category, "ru")
                                )
                            )
                        }

                        // Логируем прогресс каждые 100 упражнений
                        if ((i + 1) % 100 == 0) {
                            Log.d("ExerciseImport", "Обработано ${i + 1}/${jsonArray.length()} упражнений")
                        }

                    } catch (e: Exception) {
                        Log.e("ExerciseImport", "Ошибка при обработке упражнения $i: ${e.message}")
                        e.printStackTrace()
                    }
                }

                // Выводим метаданные для перевода
                Log.d("ExerciseImport", "========== METADATA TO TRANSLATE ==========")
                Log.d("ExerciseImport", "📊 FORCE (${forceSet.size}): ${forceSet.sorted()}")
                Log.d("ExerciseImport", "📈 LEVEL (${levelSet.size}): ${levelSet.sorted()}")
                Log.d("ExerciseImport", "⚙️ MECHANIC (${mechanicSet.size}): ${mechanicSet.sorted()}")
                Log.d("ExerciseImport", "🏋️ EQUIPMENT (${equipmentSet.size}): ${equipmentSet.sorted()}")
                Log.d("ExerciseImport", "🎯 CATEGORY (${categorySet.size}): ${categorySet.sorted()}")
                Log.d("ExerciseImport", "============================================")

                // Вставляем данные в БД
                Log.d("ExerciseImport", "Вставка данных в БД...")
                exercisesDao.insertExercisesData(exercises, exercisesTranslations, exerciseMuscles)

                val insertedCount = exercisesDao.getCount()
                Log.d("ExerciseImport", "✓ Успешно импортировано $insertedCount упражнений")
                Log.d("ExerciseImport", "✓ Добавлено ${exercisesTranslations.size} переводов")
                Log.d("ExerciseImport", "✓ Добавлено ${exerciseMuscles.size} связей с мышцами")

                insertedCount

            } catch (e: Exception) {
                // НЕ выводим jsonString в лог!
                Log.e("ExerciseImport", "❌ КРИТИЧЕСКАЯ ОШИБКА")
                Log.e("ExerciseImport", "Тип: ${e.javaClass.simpleName}")
              //  Log.e("ExerciseImport", "Сообщение: ${e.message}")
                Log.e("ExerciseImport", "Stack trace первые 5 элементов:")
                e.stackTrace.take(5).forEach {
                    Log.e("ExerciseImport", "  at ${it}")
                }
                0
            }
        }
    }

    override suspend fun getExerciseTranslations(language: String): List<ExerciseTranslations> {
        return exercisesDao.getExercisesTranslations(language)
    }

    override suspend fun getExerciseById(id: Int, language: String): ExerciseTranslations {
        return exercisesDao.getExerciseById(id, language)
    }


    override suspend fun getExerciseMusclesById(id: Int, language: String): List<ExerciseMusclesData> {
        return exercisesDao.getExerciseMuscleById(id, language)
    }
}

private fun jsonArrayToIntList(jsonArray: JSONArray?): List<Int> {
    if (jsonArray == null) return emptyList()
    val list = mutableListOf<Int>()
    for (i in 0 until jsonArray.length()) {
        list.add(jsonArray.getInt(i))
    }
    return list
}

private fun jsonArrayToList(jsonArray: JSONArray?): List<String> {
    if (jsonArray == null) return emptyList()
    val list = mutableListOf<String>()
    for (i in 0 until jsonArray.length()) {
        list.add(jsonArray.getString(i))
    }
    return list
}

fun translateForce(force: String, language: String): String {
    return when (language) {
        "ru" -> when (force) {
            "pull" -> "Тяга"
            "push" -> "Жим"
            "static" -> "Статика"
            else -> ""
        }
        "en" -> when (force) {
            "pull" -> "Pull"
            "push" -> "Push"
            "static" -> "Static"
            else -> ""
        }
        else -> force
    }
}

fun translateLevel(level: String, language: String): String {
    return when (language) {
        "ru" -> when (level) {
            "beginner" -> "Новичок"
            "intermediate" -> "Средний"
            "expert" -> "Эксперт"
            else -> ""
        }
        "en" -> when (level) {
            "beginner" -> "Beginner"
            "intermediate" -> "Intermediate"
            "expert" -> "Expert"
            else -> ""
        }
        else -> level
    }
}

fun translateMechanic(mechanic: String, language: String): String {
    return when (language) {
        "ru" -> when (mechanic) {
            "compound" -> "Базовое"
            "isolation" -> "Изолирующее"
            else -> ""
        }
        "en" -> when (mechanic) {
            "compound" -> "Compound"
            "isolation" -> "Isolation"
            else -> ""
        }
        else -> mechanic
    }
}

fun translateEquipment(equipment: String, language: String): String {
    return when (language) {
        "ru" -> when (equipment) {
            "bands" -> "Ленты"
            "barbell" -> "Штанга"
            "body only" -> "Свой вес"
            "cable" -> "Блок"
            "dumbbell" -> "Гантели"
            "e-z curl bar" -> "EZ-гриф"
            "exercise ball" -> "Фитбол"
            "foam roll" -> "Ролл"
            "kettlebells" -> "Гири"
            "machine" -> "Тренажёр"
            "medicine ball" -> "Медбол"
            "other" -> "Другое"
            else -> ""
        }
        "en" -> when (equipment) {
            "bands" -> "Bands"
            "barbell" -> "Barbell"
            "body only" -> "Body Only"
            "cable" -> "Cable"
            "dumbbell" -> "Dumbbell"
            "e-z curl bar" -> "E-Z Curl Bar"
            "exercise ball" -> "Exercise Ball"
            "foam roll" -> "Foam Roll"
            "kettlebells" -> "Kettlebells"
            "machine" -> "Machine"
            "medicine ball" -> "Medicine Ball"
            "other" -> "Other"
            else -> ""
        }
        else -> equipment
    }
}

fun translateCategory(category: String, language: String): String {
    return when (language) {
        "ru" -> when (category) {
            "cardio" -> "Кардио"
            "olympic weightlifting" -> "Тяжёлая атлетика"
            "plyometrics" -> "Плиометрика"
            "powerlifting" -> "Пауэрлифтинг"
            "strength" -> "Силовые"
            "stretching" -> "Растяжка"
            "strongman" -> "Стронгмен"
            else -> ""
        }
        "en" -> when (category) {
            "cardio" -> "Cardio"
            "olympic weightlifting" -> "Olympic Weightlifting"
            "plyometrics" -> "Plyometrics"
            "powerlifting" -> "Powerlifting"
            "strength" -> "Strength"
            "stretching" -> "Stretching"
            "strongman" -> "Strongman"
            else -> ""
        }
        else -> category
    }
}