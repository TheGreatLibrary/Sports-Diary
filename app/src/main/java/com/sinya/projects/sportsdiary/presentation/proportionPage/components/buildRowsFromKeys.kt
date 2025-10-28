package com.sinya.projects.sportsdiary.presentation.proportionPage.components

import com.sinya.projects.sportsdiary.presentation.proportionPage.ProportionRow
import com.sinya.projects.sportsdiary.presentation.proportionPage.RowItem
import com.sinya.projects.sportsdiary.presentation.proportionPage.Side

fun buildRowsFromKeys(source: List<ProportionRow>): List<RowItem> {
    val grouped =
        linkedMapOf<String, MutableMap<Side, ProportionRow>>()
    source.forEach { ui ->
        val (base, side) = parseGroup(ui.title)
        grouped.getOrPut(base) { mutableMapOf() }[side] = ui
    }

    val rows = mutableListOf<RowItem>()
    for ((base, map) in grouped) {
        val left = map[Side.LEFT]
        val right = map[Side.RIGHT]
        when {
            left != null && right != null -> rows += RowItem.PairRow(base, left, right)
            map.containsKey(Side.NONE) -> rows += RowItem.Single(base, map[Side.NONE]!!)
            left != null -> rows += RowItem.Single(base, left)
            right != null -> rows += RowItem.Single(base, right)
        }
    }
    return rows
}
