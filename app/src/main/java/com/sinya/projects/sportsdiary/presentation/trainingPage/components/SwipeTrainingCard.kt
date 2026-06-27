package com.sinya.projects.sportsdiary.presentation.trainingPage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.enums.TrainingMode
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseItem
import com.sinya.projects.sportsdiary.ui.features.RoundedIcon
import com.sinya.projects.sportsdiary.ui.features.SwipeCardBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeTrainingCard(
    modifier: Modifier = Modifier,
    item: ExerciseItem,
    onInfoClick: (Int) -> Unit,
    onPlusClick: (Int) -> Unit,
    onMinusClick: () -> Unit,
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
    onDeleteSet: (Int, Int) -> Unit,
    onModeClick: (TrainingMode, Int) -> Unit,
    onVisibleClick: (Int, Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    SwipeToDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        state = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    onMinusClick()
                }
                false
            }
        ),
        backgroundContent = {
            SwipeCardBackground()
        },
        content = {
            SwipeTrainingCardContent(
                expanded = expanded,
                onExpanded = { expanded = it },
                item = item,
                onInfoClick = onInfoClick,
                onPlusClick = onPlusClick,
                onEditSet = onEditSet,
                onDeleteSet = onDeleteSet,
                onVisibleClick = onVisibleClick,
                onModeClick = onModeClick
            )
        }
    )
}

@Composable
fun SwipeTrainingCardContent(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    item: ExerciseItem,
    onInfoClick: (Int) -> Unit,
    onPlusClick: (Int) -> Unit,
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
    onDeleteSet: (Int, Int) -> Unit,
    onModeClick: (TrainingMode, Int) -> Unit,
    onVisibleClick: (Int, Int) -> Unit
) {
    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { onExpanded(!expanded) },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderTraining(
            title = item.title,
            mode = item.mode,
            iconEye = when (item.state) {
                1 -> painterResource(R.drawable.ic_eye)
                else -> painterResource(R.drawable.ic_eye_no)
            },
            onInfoClick = { onInfoClick(item.id) },
            onModeClick = { when(item.mode) {
                TrainingMode.COUNT -> onModeClick(TrainingMode.TIME, item.id)
                TrainingMode.TIME -> onModeClick(TrainingMode.COUNT, item.id)
            }
            },
            onVisibleClick = { onVisibleClick(if (item.state==1) 0 else 1, item.id) }
        )
        TrainingCardContent(
            id = item.id,
            expanded = expanded,
            onExpanded = onExpanded,
            items = item.items,
            repsUnit = when(item.mode) {
                TrainingMode.COUNT -> stringResource(R.string.count)
                TrainingMode.TIME -> stringResource(R.string.second)
            },
            weightUnit = stringResource(R.string.kg),
            onEditSet = onEditSet,
            onPlusClick = onPlusClick,
            onDeleteSet = onDeleteSet,
        )
    }
}

@Composable
fun HeaderTraining(
    title: String,
    iconEye: Painter,
    mode: TrainingMode,
    onInfoClick: () -> Unit,
    onModeClick: () -> Unit,
    onVisibleClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.shapes.extraLarge
                )
        ) {
            RoundedIcon(
                onClick = onModeClick,
                painter = when (mode) {
                    TrainingMode.COUNT -> painterResource(R.drawable.train_muscul)
                    TrainingMode.TIME -> painterResource(R.drawable.train_time)
                },
                description = "mode",
                isSelected = true,
                size = 25.dp,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            RoundedIcon(
                onClick = onVisibleClick,
                painter = iconEye,
                description = "eye",
                isSelected = true,
                size = 25.dp,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            RoundedIcon(
                onClick = onInfoClick,
                painter = painterResource(R.drawable.ic_info),
                description = "Info",
                isSelected = true,
                size = 25.dp,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}