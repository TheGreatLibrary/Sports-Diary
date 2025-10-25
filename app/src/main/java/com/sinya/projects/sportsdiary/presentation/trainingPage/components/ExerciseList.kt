package com.sinya.projects.sportsdiary.presentation.trainingPage.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.trainingPage.ExerciseItem
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.ui.features.HeaderInfo
import com.sinya.projects.sportsdiary.ui.features.getId
import com.sinya.projects.sportsdiary.ui.features.getString

@Composable
fun ExerciseList(
    exercises: List<ExerciseItem>,
    onInfoClick: (Int) -> Unit,
    onMinusClick: (Int) -> Unit,
    onPlusClick: (Int) -> Unit,
    onDeleteSet: (Int, Int) -> Unit,
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
) {

    exercises.forEach {
        var expanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderInfo(
                title = it.title,
                onInfoClick = { onInfoClick(it.id) }
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimationIcon(
                    onClick = { onPlusClick(it.id)},
                    icon = painterResource(R.drawable.plus),
                    description = "Delete",
                    isSelected = true,
                    size = 28.dp,
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                )
                AnimationIcon(
                    onClick = { onMinusClick(it.id) },
                    icon = painterResource(R.drawable.minus),
                    description = "Delete",
                    isSelected = true,
                    size = 28.dp,
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            }
        }
        SetsEditor(
            expanded = expanded,
            onExpanded = { expanded = it },
            id = it.id,
            reps =  it.countList,
            weights = it.weightList,
            repsUnit = stringResource(R.string.count),
            weightUnit = stringResource(R.string.kg),
            onDeleteSet = onDeleteSet,
            onEditSet = onEditSet
        )
    }
}