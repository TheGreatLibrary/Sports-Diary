package com.sinya.projects.statisticdiagram.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.DropdownCheckboxItem
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.chart.MultipleChart
import com.sinya.projects.ui.components.chart.model.AxisData
import com.sinya.projects.ui.components.dropdown.DropdownCheckboxMenu
import com.sinya.projects.ui.components.dropdown.components.DropdownElementCheckBox

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
internal fun MultiChartWithDropdown(
    items: List<DropdownCheckboxItem>,
    title: String,
    onToggle: (Int) -> Unit,
    onInfoClick: () -> Unit,
    onChartInfoClick: () -> Unit,
    axisData: AxisData
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DropdownCheckboxMenu(
            title = title,
            items = items,
            expanded = expanded,
            selectedItems = items.filter { it.checked },
            nameObject = { list ->
                list.filter { it.checked }.joinToString { it.title }.ifEmpty { context.getString(R.string.not_category) }
            },
            onExpanded = { expanded = it },
            onInfoClick = onInfoClick
        ) { item ->
            DropdownElementCheckBox(
                title = item.title,
                checked = item.checked,
                onClick = { onToggle(item.id) }
            )
        }
        MultipleChart(
            title = stringResource(com.sinya.projects.ui.R.string.grown_up_trainings),
            onInfoClick = onChartInfoClick,
            axisData = axisData
        )
    }
}