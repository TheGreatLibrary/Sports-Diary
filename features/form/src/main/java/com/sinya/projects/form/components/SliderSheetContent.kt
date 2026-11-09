package com.sinya.projects.form.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
internal fun SliderSheetContent(
    title: String,
    unit: String,
    initialValue: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    stepSize: Int,
    onConfirm: (Int) -> Unit
) {
    var currentValue by remember { mutableIntStateOf(initialValue) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "$currentValue $unit",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = currentValue.toFloat(),
            onValueChange = { raw ->
                currentValue = ((raw / stepSize).roundToInt() * stepSize)
                    .coerceIn(valueRange.start.toInt(), valueRange.endInclusive.toInt())
            },
            valueRange = valueRange
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onConfirm(currentValue) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Готово")
        }
    }
}
