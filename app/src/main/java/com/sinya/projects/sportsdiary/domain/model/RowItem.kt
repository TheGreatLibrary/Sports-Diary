package com.sinya.projects.sportsdiary.domain.model

import com.sinya.projects.sportsdiary.domain.enums.Side
import com.sinya.projects.sportsdiary.utils.parseGroup

data class MeasureItem(
    val headerKey: String,
    val items: List<ProportionRow>,
    val infoId: Int
)

fun List<ProportionRow>.buildMeasureItemsFromKeys(): List<MeasureItem> {
    val grouped = linkedMapOf<String, MutableList<Pair<Side, ProportionRow>>>()

    forEach { row ->
        val (base, side) = parseGroup(row.title)
        grouped.getOrPut(base) { mutableListOf() } += side to row
    }

    return grouped.map { (base, entries) ->
        val common = entries.firstOrNull { it.first == Side.NONE }?.second
        val items = entries
            .filter { it.first != Side.NONE }
            .map { it.second }

        MeasureItem(
            headerKey = base,
            items = items.ifEmpty { listOfNotNull(common) },
            infoId = common?.id ?: items.first().id
        )
    }
}