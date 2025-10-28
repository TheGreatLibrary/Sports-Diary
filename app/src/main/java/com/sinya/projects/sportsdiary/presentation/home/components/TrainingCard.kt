package com.sinya.projects.sportsdiary.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.trainings.Training
import com.sinya.projects.sportsdiary.ui.features.AnimationCard
import com.sinya.projects.sportsdiary.utils.getString
import kotlinx.coroutines.launch

@Composable
fun TrainingCard(
    trainingList: List<Training>,
    onTrainingClick: () -> Unit,
    onTrainingPlusClick: () -> Unit,
    onTrainingCardClick: (Int) -> Unit,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    if (trainingList.isNotEmpty()) {
        LaunchedEffect(trainingList.size) {
            coroutineScope.launch {
                listState.scrollToItem(trainingList.lastIndex)
            }
        }
    }

    AnimationCard(
        modifier = Modifier.height(160.dp),
        onClick = onTrainingClick,
        colorCard = MaterialTheme.colorScheme.secondary
    ) {
        CardHome(
            title = stringResource(R.string.training_title),
            onPlusClick = onTrainingPlusClick,
            content = {
                if (trainingList.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(trainingList) { _, it ->
                            AnimationCard(
                                onClick = { onTrainingCardClick(it.id) },
                                shapeCard = MaterialTheme.shapes.extraSmall,
                                colorCard = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = "Тренировка на ${if (it.categoryId==1) context.getString(it.category) else it.category} №${it.name} - ${it.date}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 4.dp
                                    ),
                                )
                            }
                        }
                    }
                }
                else {
                    Text(
                        text = stringResource(R.string.not_entries_of_training),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 30.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }
}