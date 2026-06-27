package com.sinya.projects.sportsdiary.core.domain.model

import com.sinya.projects.sportsdiary.core.domain.enums.TrainingMode

data class ExerciseItem(
    val id: Int,
    val title: String,
    val state: Int,
    val mode: TrainingMode,
    val orderIndex: Int,
    val items: List<ExerciseItemData> = listOf()
)

fun ExerciseItem.toExerciseData(): ExerciseData {
    val counts = items.map { it.count }
    val weights = items.map { it.weight }
    val prevCounts = items.mapNotNull { it.prevCount }
    val prevWeights = items.mapNotNull { it.prevWeight }

    return ExerciseData(
        id = id,
        title = title,
        countResult = counts.joinToString("/"),
        weightResult = weights.joinToString("/"),
        prevCountResult = prevCounts.joinToString("/"),
        prevWeightResult = prevWeights.joinToString("/"),
        state = state,
        mode = mode.mode,
        orderIndex = orderIndex
    )
}

fun ExerciseItem.updateSet(
    setIndex: Int,
    newCount: String? = null,
    newWeight: String? = null
): ExerciseItem {
    require(setIndex in items.indices) { "Индекс подхода $setIndex вне диапазона" }

    val updatedItems = items.mapIndexed { index, itemData ->
        if (index == setIndex) {
            itemData.copy(
                count = newCount ?: itemData.count,
                weight = newWeight ?: itemData.weight
            )
        } else {
            itemData
        }
    }

    return this.copy(items = updatedItems)
}

fun ExerciseItem.addEmptySet(): ExerciseItem {
    val newIndex = items.size
    val newItem =
        ExerciseItemData(
            exerciseId = id,
            index = newIndex,
            count = "0",
            weight = "0"
        )

    return this.copy(items = items + newItem)
}

fun ExerciseItem.removeSet(setIndex: Int): ExerciseItem {
    require(setIndex in items.indices) { "Индекс подхода $setIndex вне диапазона" }

    return this.copy(items = items.filterIndexed { index, _ -> index != setIndex })
        .let { item ->
            item.copy(items = item.items.mapIndexed { newIndex, itemData ->
                itemData.copy(index = newIndex)
            })
        }
}

fun ExerciseItem.insertSet(setIndex: Int, exercise: ExerciseItemData): ExerciseItem {
    val newList = this.items.toMutableList()
    newList.add(setIndex, exercise)
    require(setIndex in items.indices) { "Индекс подхода $setIndex вне диапазона" }

    return this.copy(items = newList).let { item ->
        item.copy(items = item.items.mapIndexed { newIndex, itemData ->
            itemData.copy(index = newIndex)
        })
    }
}