package com.sinya.projects.sportsdiary.presentation.proportionPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.trainingPage.components.deltaFloat
import com.sinya.projects.sportsdiary.ui.features.HeaderInfo
import com.sinya.projects.sportsdiary.ui.features.getString
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.CompactUnitField

@Composable
fun ProportionPageScreen(
    state: ProportionPageUiState,
    onEvent: (ProportionPageUiEvent) -> Unit,
    onInfoClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    when (state) {
        is ProportionPageUiState.Loading -> PlaceholderScreen()
        is ProportionPageUiState.Success -> ProportionPage(
            title = state.item.title,
            onInfoClick = onInfoClick,
            onBackClick = onBackClick,
            proportion = state.item,
            onEvent = onEvent
        )
        is ProportionPageUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun ProportionPage(
    title: String,
    onInfoClick: () -> Unit,
    onBackClick: () -> Unit,
    proportion: ProportionItem,
    onEvent: (ProportionPageUiEvent) -> Unit,
) {
    val context = LocalContext.current
    val rows = remember(proportion) { buildRowsFromKeys(proportion.items) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            title = title,
            isVisibleBack = true,
            onBackClick = onBackClick,
            isVisibleSave = true,
            onSaveClick = { onEvent(ProportionPageUiEvent.Save) }
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(rows.size) { idx ->
                when (val row = rows[idx]) {
                    is RowItem.Single -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HeaderInfo(
                                title = context.getString(row.headerKey),
                                onInfoClick = onInfoClick
                            )
                            CompactUnitField(
                                value = row.item.value,
                                onValueChange = { str -> onEvent(ProportionPageUiEvent.OnChangeValue(row.item.id, str)) },
                                unit = context.getString(row.item.unitMeasure),
                                modifier = Modifier.fillMaxWidth(),
                                delta = deltaFloat(row.item.value, null),
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                    }
                    is RowItem.PairRow -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HeaderInfo(
                                title = context.getString(row.headerKey),
                                onInfoClick = onInfoClick
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CompactUnitField(
                                    value = row.left.value,
                                    onValueChange = { str -> onEvent(ProportionPageUiEvent.OnChangeValue(row.left.id, str)) },
                                    unit = context.getString(row.left.unitMeasure),
                                    modifier = Modifier.weight(1f),
                                    delta = deltaFloat(row.left.value, null),
                                    keyboardType = KeyboardType.Decimal
                                )
                                CompactUnitField(
                                    value = row.right.value,
                                    onValueChange = { str -> onEvent(ProportionPageUiEvent.OnChangeValue(row.right.id, str)) },
                                    unit = context.getString(row.right.unitMeasure),
                                    modifier = Modifier.weight(1f),
                                    delta = deltaFloat(row.right.value, null),
                                    keyboardType = KeyboardType.Decimal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun buildRowsFromKeys(source: List<ProportionRow>): List<RowItem> {
    val grouped = linkedMapOf<String, MutableMap<Side, ProportionRow>>() // baseKey -> {side -> item}
    source.forEach { ui ->
        val (base, side) = parseGroup(ui.title)
        grouped.getOrPut(base) { mutableMapOf() }[side] = ui
    }

    val rows = mutableListOf<RowItem>()
    for ((base, map) in grouped) {
        val left  = map[Side.LEFT]
        val right = map[Side.RIGHT]
        when {
            left != null && right != null -> rows += RowItem.PairRow(base, left, right)
            map.containsKey(Side.NONE)    -> rows += RowItem.Single(base, map[Side.NONE]!!)
            left != null                  -> rows += RowItem.Single(base, left)
            right != null                 -> rows += RowItem.Single(base, right)
        }
    }
    return rows
}

private fun parseGroup(nameKey: String): Pair<String, Side> = when {
    nameKey.endsWith("_left")  -> nameKey.removeSuffix("_left")  to Side.LEFT
    nameKey.endsWith("_right") -> nameKey.removeSuffix("_right") to Side.RIGHT
    else                       -> nameKey                         to Side.NONE
}

private sealed class RowItem {
    data class Single(
        val headerKey: String,        // ключ ресурса заголовка
        val item: ProportionRow
    ) : RowItem()

    data class PairRow(
        val headerKey: String,        // baseKey: "biceps"
        val left: ProportionRow,
        val right:ProportionRow
    ) : RowItem()
}