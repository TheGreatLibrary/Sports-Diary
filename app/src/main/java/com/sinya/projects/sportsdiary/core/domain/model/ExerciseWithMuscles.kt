package com.sinya.projects.sportsdiary.core.domain.model

import androidx.compose.runtime.Stable
import com.sinya.projects.sportsdiary.core.domain.enums.TypeCustom

@Stable
data class ExerciseWithMuscles(
    val id: Int,
    val name: String,
    val level: String?,
    val equipment: String?,
    val category: String?,
    val muscles: List<String>,
    val checked: Boolean = false,
    val isCustom: Boolean = false
) {
    val musclesText: String by lazy {
        muscles.joinToString(", ")
    }
}

fun List<ExerciseWithMuscles>.filterByCategory(category: String?): List<ExerciseWithMuscles> {
    return this
        .filter { training ->
            category.isNullOrEmpty() || training.category == category || (category == "no one" && training.category.isNullOrEmpty())
        }
}

fun List<ExerciseWithMuscles>.filterByMuscle(muscle: String?): List<ExerciseWithMuscles> {
    return this
        .filter { exercise ->
            muscle.isNullOrEmpty() ||
                    exercise.muscles.any { it.equals(muscle, ignoreCase = true) } ||
                    (muscle == "no one" && exercise.muscles.isEmpty())
        }
}

fun List<ExerciseWithMuscles>.filterByLevel(level: String?): List<ExerciseWithMuscles> {
    return this
        .filter { training ->
            level.isNullOrEmpty() || training.level == level || (level == "no one" && training.level.isNullOrEmpty())
        }
}

fun List<ExerciseWithMuscles>.filterByCustom(isCustom: TypeCustom): List<ExerciseWithMuscles> {
    return this
        .filter { training ->
            training.isCustom == isCustom.state || (isCustom == TypeCustom.ALL)
        }
}

fun List<ExerciseWithMuscles>.filterByEquipment(equipment: String?): List<ExerciseWithMuscles> {
    return this
        .filter { training ->
            equipment.isNullOrEmpty() || training.equipment == equipment || (equipment == "no one" && training.equipment.isNullOrEmpty())

        }
}

val List<ExerciseWithMuscles>.categories: List<String?>
    get() = this
        .map { it.category }
        .distinct()

val List<ExerciseWithMuscles>.custom: List<Boolean?>
    get() = this
        .map { it.isCustom }
        .distinct()

val List<ExerciseWithMuscles>.equipment: List<String?>
    get() = this
        .map { it.equipment }
        .distinct()

val List<ExerciseWithMuscles>.level: List<String?>
    get() = this
        .map { it.level }
        .distinct()

val List<ExerciseWithMuscles>.allMuscles: List<String>
    get() = this
        .flatMap { it.muscles }
        .distinct()
        .sorted()
