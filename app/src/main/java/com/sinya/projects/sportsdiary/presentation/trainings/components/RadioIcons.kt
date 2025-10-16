package com.sinya.projects.sportsdiary.presentation.trainings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon

@Composable
fun RadioIcons(
    onMuscleClick: () -> Unit,
    onTimeClick: () -> Unit,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.sorted_about),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        AnimationIcon(
            onClick = onTimeClick,
            description = "Time",
            icon = painterResource(R.drawable.train_time),
            isSelected = isSelected,
            size = 26.dp,
            shape = MaterialTheme.shapes.extraSmall,
            selectedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedContentColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedContentColor = MaterialTheme.colorScheme.onPrimary
        )
        AnimationIcon(
            onClick = onMuscleClick,
            description = "Muscle",
            icon = painterResource(R.drawable.train_muscul),
            isSelected = !isSelected,
            size = 26.dp,
            shape = MaterialTheme.shapes.extraSmall,
            selectedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedContentColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedContentColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}