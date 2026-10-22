package com.sinya.projects.pageproportion.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.ProportionRow
import com.sinya.projects.domain.utils.deltaFloat
import com.sinya.projects.domain.utils.getString
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.HeaderOfUnitField
import com.sinya.projects.ui.components.trainingConstructor.CompactUnitField

@Composable
internal fun MeasureCard(
    title: String,
    onInfoClick: () -> Unit,
    items: List<ProportionRow>,
    onValueChange: (Int, String, String) -> Unit,
    context: Context,
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderOfUnitField(
            title = title,
            icon = R.drawable.ic_info,
            onInfoClick = onInfoClick
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                CompactUnitField(
                    value = item.value,
                    onValueChange = { str ->
                        onValueChange(item.id, item.value, str)
                    },
                    unit = context.getString(item.unitMeasure),
                    modifier = Modifier.weight(1f),
                    delta = deltaFloat(item.value, item.delta),
                    keyboardType = KeyboardType.Decimal,
                )
            }
        }
    }
}