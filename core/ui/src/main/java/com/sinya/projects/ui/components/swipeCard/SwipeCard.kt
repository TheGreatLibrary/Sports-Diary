package com.sinya.projects.ui.components.swipeCard

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.sinya.projects.ui.components.swipeCard.components.SwipeBackground
import com.sinya.projects.ui.components.swipeCard.components.SwipeContent

@Composable
fun SwipeCard(
    modifier: Modifier,
    @DrawableRes icon: Int?,
    @DrawableRes startIconBack: Int?,
    @DrawableRes endIconBack: Int,
    title: String,
    description: String,
    selected: Boolean,
    onSelected: () -> Unit,
    onClick: () -> Unit,
    selectionState: Boolean,
    swipeToDelete: () -> Unit,
    swipeToChange: (() -> Unit)?,
    onMenuClick: () -> Unit
) {
    SwipeToDismissBox(
        modifier = modifier.fillMaxWidth(),
        state = rememberSwipeToDismissBoxState(
            positionalThreshold = { totalDistance ->
                totalDistance * 0.25f
            },
            confirmValueChange = {
                when (it) {
                    SwipeToDismissBoxValue.EndToStart -> swipeToDelete()
                    SwipeToDismissBoxValue.StartToEnd -> swipeToChange?.invoke()
                    else -> Unit
                }
                false
            }
        ),
        enableDismissFromEndToStart = !selectionState,
        enableDismissFromStartToEnd = !selectionState && swipeToChange != null,
        backgroundContent = {
            SwipeBackground(
                modifier = Modifier,
                iconStart = startIconBack,
                iconEnd = endIconBack,
                colorStart = MaterialTheme.colorScheme.onPrimary,
                colorEnd = MaterialTheme.colorScheme.onPrimary,
            )
        },
        content = {
            SwipeContent(
                title = title,
                description = description,
                selected = selected,
                onSelected = onSelected,
                onClick1 = onClick,
                selectionState = selectionState,
                icon = icon,
                onMenuClick = onMenuClick,
                modifier = Modifier
            )
        }
    )
}