package com.sinya.projects.sportsdiary.domain.model

data class ExerciseItem(
    val id: Int,
    val title: String,
    val state: Int,
    val orderIndex: Int,
    val item: List<ExerciseItemData> = listOf()
)

fun ExerciseItem.toExerciseData(): ExerciseData {
    val counts = item.map { it.count }
    val weights = item.map { it.weight }
    val prevCounts = item.mapNotNull { it.prevCount }
    val prevWeights = item.mapNotNull { it.prevWeight }

    return ExerciseData(
        id = id,
        title = title,
        countResult = counts.joinToString("/"),
        weightResult = weights.joinToString("/"),
        prevCountResult = prevCounts.joinToString("/"),
        prevWeightResult = prevWeights.joinToString("/"),
        state = state,
        orderIndex = orderIndex
    )
}

fun ExerciseItem.updateSet(
    setIndex: Int,
    newCount: String? = null,
    newWeight: String? = null
): ExerciseItem {
    require(setIndex in item.indices) { "Индекс подхода $setIndex вне диапазона" }

    val updatedItems = item.mapIndexed { index, itemData ->
        if (index == setIndex) {
            itemData.copy(
                count = newCount ?: itemData.count,
                weight = newWeight ?: itemData.weight
            )
        } else {
            itemData
        }
    }

    return this.copy(item = updatedItems)
}

fun ExerciseItem.addEmptySet(): ExerciseItem {
    val newIndex = item.size
    val newItem = ExerciseItemData(
        exerciseId = id,
        index = newIndex,
        count = "0",
        weight = "0"
    )

    return this.copy(item = item + newItem)
}

fun ExerciseItem.removeSet(setIndex: Int): ExerciseItem {
    require(setIndex in item.indices) { "Индекс подхода $setIndex вне диапазона" }

    return this.copy(item = item.filterIndexed { index, _ -> index != setIndex })
        // Переиндексируем оставшиеся подходы
        .let { item ->
            item.copy(item = item.item.mapIndexed { newIndex, itemData ->
                itemData.copy(index = newIndex)
            })
        }
}