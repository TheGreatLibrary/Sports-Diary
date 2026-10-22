package com.sinya.projects.ui.components.sort

import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.ui.R


enum class TypeCustom(
    val state: Boolean?,
    val stringRes: Int
) {
    ALL(null, R.string.all),
    BASED(false, R.string.base_exercise),
    CUSTOM(true, R.string.custom);

    companion object {
        fun getType(state: Boolean?): TypeCustom {
            return when (state) {
                true -> CUSTOM
                false -> BASED
                else -> ALL
            }
        }
    }
}

fun List<ExerciseWithMuscles>.filterByCustom(isCustom: TypeCustom): List<ExerciseWithMuscles> {
    return this
        .filter { training ->
            training.isCustom == isCustom.state || (isCustom == TypeCustom.ALL)
        }
}
