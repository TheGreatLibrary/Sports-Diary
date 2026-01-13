package com.sinya.projects.sportsdiary.domain.model

data class ExerciseData(
    val id: Int,
    val title: String,
    val countResult: String,
    val weightResult: String,
    val prevCountResult: String,
    val prevWeightResult: String,
    val state: Int,
    val orderIndex: Int
)

fun ExerciseData.toItem(): ExerciseItem {
    val counts = countResult.split('/')
    val weights = weightResult.split('/')
    val prevCounts = prevCountResult.split('/')
    val prevWeights = prevWeightResult.split('/')

    val maxSets = maxOf(
        counts.size,
        weights.size,
        prevCounts.size,
        prevWeights.size
    )

    val items = List(maxSets) { index ->
        ExerciseItemData(
            exerciseId = this.id,
            index = index,
            count = counts.getOrNull(index)?.takeIf { it.isNotBlank() } ?: "0",
            weight = weights.getOrNull(index)?.takeIf { it.isNotBlank() } ?: "0",
            prevCount = prevCounts.getOrNull(index)?.takeIf { it.isNotBlank() },
            prevWeight = prevWeights.getOrNull(index)?.takeIf { it.isNotBlank() }
        )
    }

    return ExerciseItem(
        id = this.id,
        title = this.title,
        state = this.state,
        orderIndex = this.orderIndex,
        item = items
    )
}

