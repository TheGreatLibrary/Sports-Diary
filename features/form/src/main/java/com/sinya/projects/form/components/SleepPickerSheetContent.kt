package com.sinya.projects.form.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun SleepPickerSheetContent(
    initialMinutes: Int,
    onConfirm: (Int) -> Unit
) {
    val hoursList = remember { (4..12).toList() }
    val minutesList = remember { listOf(0, 15, 30, 45) }

    var selectedHours by remember { mutableIntStateOf((initialMinutes / 60).coerceIn(4, 12)) }
    var selectedMins by remember { mutableIntStateOf(((initialMinutes % 60) / 15) * 15) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Продолжительность сна", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelPicker(
                items = hoursList.map { "$it ч" },
                initialIndex = hoursList.indexOf(selectedHours),
                modifier = Modifier.weight(1f),
                onItemSelected = { selectedHours = it.removeSuffix(" ч").toInt() }
            )
            WheelPicker(
                items = minutesList.map { "$it мин" },
                initialIndex = minutesList.indexOf(selectedMins).coerceAtLeast(0),
                modifier = Modifier.weight(1f),
                onItemSelected = { selectedMins = it.removeSuffix(" мин").toInt() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onConfirm(selectedHours * 60 + selectedMins) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Готово")
        }
    }
}
