package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeCard(
    modifier: Modifier,
    id: Int,
    title: String,
    description: String? = null,
    onDelete: (Int) -> Unit,
    onTrainingClick: () -> Unit,
) {
    SwipeToDismissBox(
        modifier = modifier.fillMaxWidth(),
        state = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    onDelete(id)
                }
                false
            }
        ),
        backgroundContent = {
            SwipeCardBackground()
        },
        content = {
            SwipeCardContent(
                title = title,
                description = description,
                onTrainingClick = onTrainingClick
            )
        }

    )
}

@Composable
fun SwipeCardContent(
    modifier: Modifier = Modifier,
    title: String,
    description: String?,
    onTrainingClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
            .clickable { onTrainingClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        description?.let {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
