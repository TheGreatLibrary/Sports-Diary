package com.sinya.projects.form.components


import androidx.compose.foundation.layout.Arrangement
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
import kotlin.math.roundToInt

@Composable
internal fun WeightPickerSheetContent(
    initialWeight: Float,
    onConfirm: (Float) -> Unit
) {
    val kilosList = remember { (30..200).toList() }
    val gramsList = remember { (0..9).toList() }

    var selectedKilo by remember { mutableIntStateOf(initialWeight.toInt().coerceIn(30, 200)) }
    var selectedGram by remember { mutableIntStateOf(((initialWeight - initialWeight.toInt()) * 10).roundToInt().coerceIn(0, 9)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Желаемый вес (кг)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelPicker(
                items = kilosList,
                initialIndex = kilosList.indexOf(selectedKilo),
                modifier = Modifier.weight(1f),
                onItemSelected = { selectedKilo = it }
            )
            Text(".", style = MaterialTheme.typography.titleLarge)
            WheelPicker(
                items = gramsList,
                initialIndex = gramsList.indexOf(selectedGram),
                modifier = Modifier.weight(1f),
                onItemSelected = { selectedGram = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onConfirm(selectedKilo + selectedGram / 10f) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Готово")
        }
    }
}
