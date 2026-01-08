package com.sinya.projects.sportsdiary.presentation.proportionPage.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.domain.model.ProportionRow
import com.sinya.projects.sportsdiary.ui.features.HeaderInfo
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.CompactUnitField
import com.sinya.projects.sportsdiary.utils.deltaFloat
import com.sinya.projects.sportsdiary.utils.getString

@Composable
fun MeasureCard(
    title: String,
    onInfoClick: () -> Unit,
    items: List<ProportionRow>,
    onValueChange: (Int, String) -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderInfo(
            title = title,
            onInfoClick = onInfoClick
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                CompactUnitField(
                    value = item.value,
                    onValueChange = { str ->
                        onValueChange(item.id, str)
                    },
                    unit = context.getString(item.unitMeasure),
                    modifier = Modifier.weight(1f),
                    delta = deltaFloat(item.value, item.delta),
                    keyboardType = KeyboardType.Decimal
                )
            }
        }
    }
}