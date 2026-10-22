package com.sinya.projects.domain.utils

import com.sinya.projects.domain.enums.TrainingMode
import com.sinya.projects.domain.model.DataExerciseMuscleRaw

fun calculateTonnagePerMuscle(rawList: List<DataExerciseMuscleRaw>): Map<Int, Float> {
    return rawList.groupBy { it.muscleId }.mapValues { (_, entries) ->
        var totalWeight = 0f

        entries.forEach { item ->
            val countArr = item.countResult.split('/')
            val weightArr = item.weightResult.split('/')

            for (i in countArr.indices) {
                val count = countArr.getOrNull(i)?.toFloatOrNull() ?: 0f
                val weight = weightArr.getOrNull(i)?.toFloatOrNull() ?: 0f

                totalWeight += count * (weight + item.weight) / if (TrainingMode.getMode(item.mode) == TrainingMode.TIME) 4f else 1f
            }
        }
        totalWeight / 1000f
    }
}
