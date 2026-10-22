package com.sinya.projects.pagetraining.form.components.card.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.swipeCard.components.SwipeBackground
import com.sinya.projects.ui.components.trainingConstructor.CompactUnitField

@Composable
internal fun SwipeTrainingStepCard(
    rep: String,
    weight: String,
    repsUnit: String,
    weightUnit: String,
    onRepChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    deltaRep: Float?,
    deltaWeight: Float?,
    onRemove: () -> Unit,
    onPlusClick: () -> Unit
) {
    SwipeToDismissBox(
        modifier = Modifier.fillMaxWidth(),
        state = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    onRemove()
                } else if (it == SwipeToDismissBoxValue.StartToEnd) {
                    onPlusClick()
                }
                false
            }
        ),
        backgroundContent = {
            SwipeBackground(
                iconStart = R.drawable.ic_plus,
                iconEnd = R.drawable.morn_trash,
                colorStart = MaterialTheme.colorScheme.primary
            )
        },
        content = {
            TrainingStepCardContent(
                rep = rep,
                weight = weight,
                repsUnit = repsUnit,
                weightUnit = weightUnit,
                onRepChange = onRepChange,
                onWeightChange = onWeightChange,
                deltaRep = deltaRep,
                deltaWeight = deltaWeight
            )
        }
    )
}

@Composable
private fun TrainingStepCardContent(
    rep: String,
    weight: String,
    repsUnit: String,
    weightUnit: String,
    onRepChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    deltaRep: Float?,
    deltaWeight: Float?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CompactUnitField(
            value = rep,
            onValueChange = onRepChange,
            unit = repsUnit,
            modifier = Modifier.weight(1f),
            delta = deltaRep,
            keyboardType = KeyboardType.Decimal,
            shape = RoundedCornerShape(20.dp, 20.dp, 5.dp, 20.dp)
        )
        CompactUnitField(
            value = weight,
            onValueChange = onWeightChange,
            unit = weightUnit,
            modifier = Modifier.weight(1f),
            delta = deltaWeight,
            keyboardType = KeyboardType.Decimal,
            shape = RoundedCornerShape(5.dp, 20.dp, 20.dp, 20.dp)
        )
    }
}
