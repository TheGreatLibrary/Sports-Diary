package com.sinya.projects.sportsdiary.presentation.trainings.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.trainings.Training
import com.sinya.projects.sportsdiary.ui.features.AnimationCard
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.utils.getString

@Composable
fun TrainingCard(
    t: Training,
    rowFill: Float,
    onMinusClick: (Int) -> Unit,
    onTrainingClick: () -> Unit,
    context: Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(rowFill-0.1f),
        ) {
            AnimationCard(
                onClick = onTrainingClick,
                shapeCard = MaterialTheme.shapes.small,
                colorCard = MaterialTheme.colorScheme.primaryContainer,
            ) {
                val dateText = t.localDateOrNull()?.format(dateFmt) ?: t.date

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Тренировка ${t.name}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "${context.getString(t.category)} • $dateText",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        Button(
            modifier = Modifier.height(30.dp).width(40.dp),
            onClick = { onMinusClick(t.id) },
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