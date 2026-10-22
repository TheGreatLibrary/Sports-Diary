package com.sinya.projects.calculator.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.ProportionRow
import com.sinya.projects.domain.utils.getString
import com.sinya.projects.ui.components.trainingConstructor.CompactUnitField

@Composable
internal fun OutputCalculateCard(
    title: String,
    item: ProportionRow,
    onValueChange: (Int, String, String) -> Unit,
    context: Context,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.weight(0.4f),
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        CompactUnitField(
            value = item.value,
            onValueChange = { str -> onValueChange(item.id, item.value, str) },
            unit = context.getString(item.unitMeasure),
            modifier = Modifier.weight(0.6f),
            readOnly = true,
            delta = null,
            keyboardType = KeyboardType.Decimal,
        )
    }
}