package com.sinya.projects.sportsdiary.utils

import com.sinya.projects.sportsdiary.domain.enums.Side

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

