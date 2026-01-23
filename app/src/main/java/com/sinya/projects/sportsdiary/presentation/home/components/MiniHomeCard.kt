package com.sinya.projects.sportsdiary.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.model.Training
import com.sinya.projects.sportsdiary.main.ScreenRoute
import com.sinya.projects.sportsdiary.ui.features.AnimationCard
import kotlinx.coroutines.launch

@Composable
fun MiniHomeCard(
    modifier: Modifier = Modifier,
    description: String,
    title: String,
    list: List<Training>,
    colorCard: Color,
    onCardClick: (ScreenRoute) -> Unit,
    onPlusClick: (() -> Unit)? = null,
    onItemClick: (Int) -> Unit
) {
    HomeCard(
        modifier = modifier,
        title = title,
        onCardClick = onCardClick,
        onPlusClick = onPlusClick,
        colorCard = colorCard
    ) {
        if (list.isNotEmpty()) {
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(list.size) {
                coroutineScope.launch {
                    listState.scrollToItem(list.lastIndex)
                }
            }

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(list) { item ->
                    MeasurementItem(
                        item = item,
                        onClick = { onItemClick(item.id) }
                    )
                }

            }
        } else {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun MeasurementItem(
    item: Training,
    onClick: (Int) -> Unit
) {
    AnimationCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(item.id) },
        shapeCard = MaterialTheme.shapes.extraSmall,
        colorCard = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Text(
            text = stringResource(
                R.string.workout_type_training,
                item.category ?: stringResource(R.string.not_category),
                item.name,
                item.date
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
        )
    }
}