package com.sinya.projects.sportsdiary.presentation.proportions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.presentation.proportions.Proportion
import com.sinya.projects.sportsdiary.ui.features.AnimationCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProportionCard(
    t: Proportion,
    rowFill: Float,
    onTrainingClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(rowFill)
    ) {
        AnimationCard(
            onClick = onTrainingClick,
            shapeCard = MaterialTheme.shapes.small,
            colorCard = MaterialTheme.colorScheme.primaryContainer,
        ) {
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru"))

            val dateText = try {
                LocalDate.parse(t.date, inputFormatter)?.format(outputFormatter)
            } catch (e: Exception) {
                t.date // в случае ошибки оставляем оригинальную строку
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Замер ${t.title}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = dateText?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}