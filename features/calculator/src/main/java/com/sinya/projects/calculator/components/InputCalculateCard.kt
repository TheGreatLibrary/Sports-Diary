package com.sinya.projects.calculator.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.ProportionRow
import com.sinya.projects.domain.utils.getString
import com.sinya.projects.ui.components.trainingConstructor.CompactUnitField


@Composable
internal fun InputCalculateCard(
    title: String,
    item: ProportionRow,
    onValueChange: (Int, String, String) -> Unit,
    context: Context,
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.fillMaxWidth(),
            delta = null,
            keyboardType = KeyboardType.Decimal,
        )
    }
}