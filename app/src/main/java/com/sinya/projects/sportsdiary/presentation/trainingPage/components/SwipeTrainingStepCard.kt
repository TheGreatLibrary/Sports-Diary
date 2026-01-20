package com.sinya.projects.sportsdiary.presentation.trainingPage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.ui.features.SwipeCardBackground
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.CompactUnitField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeTrainingStepCard(
    rep: String,
    weight: String,
    repsUnit: String,
    weightUnit: String,
    onRepChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    deltaRep: Int?,
    deltaWeight: Int?,
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
            SwipeCardBackground(painter1 = painterResource(R.drawable.ic_plus))
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
    deltaRep: Int?,
    deltaWeight: Int?
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
    }
}
