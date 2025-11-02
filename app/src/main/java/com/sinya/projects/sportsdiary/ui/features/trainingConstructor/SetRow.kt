package com.sinya.projects.sportsdiary.ui.features.trainingConstructor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R

@Composable
fun SetRow(
    rep: String,
    weight: String,
    repsUnit: String,
    weightUnit: String,
    onRepChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    deltaRep: Int?,
    deltaWeight: Int?,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompactUnitField(
            value = rep,
            onValueChange = onRepChange,
            unit = repsUnit,
            modifier = Modifier.weight(1f),
            delta = deltaRep,
            keyboardType = KeyboardType.Number
        )
        CompactUnitField(
            value = weight,
            onValueChange = onWeightChange,
            unit = weightUnit,
            modifier = Modifier.weight(1f),
            delta = deltaWeight,
            keyboardType = KeyboardType.Decimal
        )
        Button(
            modifier = Modifier.height(30.dp).width(40.dp),
            onClick = onRemove,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_minus),
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}