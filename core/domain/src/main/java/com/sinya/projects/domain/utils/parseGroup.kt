package com.sinya.projects.domain.utils

import com.sinya.projects.domain.enums.Side
import com.sinya.projects.domain.model.DropdownCheckboxItem

fun parseGroup(nameKey: String): Pair<String, Side> = when {
    nameKey.endsWith(" Left") -> nameKey.removeSuffix(" Left") to Side.LEFT
    nameKey.endsWith(" левый") -> nameKey.removeSuffix(" левый") to Side.LEFT
    nameKey.endsWith(" левая") -> nameKey.removeSuffix(" левая") to Side.LEFT
    nameKey.endsWith(" левое") -> nameKey.removeSuffix(" левое") to Side.LEFT

    nameKey.endsWith(" Right") -> nameKey.removeSuffix(" Right") to Side.RIGHT
    nameKey.endsWith(" правый") -> nameKey.removeSuffix(" правый") to Side.RIGHT
    nameKey.endsWith(" правая") -> nameKey.removeSuffix(" правая") to Side.RIGHT
    nameKey.endsWith(" правое") -> nameKey.removeSuffix(" правое") to Side.RIGHT
    else -> nameKey to Side.NONE
}

fun List<DropdownCheckboxItem>.getSortedList() : List<DropdownCheckboxItem> {
    val rules = listOf(" Left", " Right", " левый", " левое", " левая", " правая", " правый", " правое")
    val measuresWithDescription = mutableSetOf<DropdownCheckboxItem>()
    val measuresBased = mutableSetOf<DropdownCheckboxItem>()

    this.forEach { item ->
        rules.forEach {
            if (item.title.contains(it)) {
                measuresWithDescription.add(item)
                return@forEach
            }
        }
    }
    val measuresAnother = this - measuresWithDescription

    measuresAnother.forEach { item ->
        measuresWithDescription.forEach { newItem ->
            if (newItem.title.contains(item.title)) {
                measuresBased.add(item)
                return@forEach
            }
        }
    }

    return (this-measuresBased).toList()
}
