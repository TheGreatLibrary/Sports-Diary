package com.sinya.projects.notes.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sinya.projects.ui.components.swipeCard.components.SwipeBackground

@Composable
internal fun NoteCard(
    modifier: Modifier,
    note: String,
    swipeToEdit: () -> Unit,
    onValueChange: (String) -> Unit,
    onSelect: () -> Unit,
    selectionState: Boolean = false,
    selected: Boolean,
    onMenuClick: () -> Unit,
    @DrawableRes startIconBack: Int?,
    @DrawableRes endIconBack: Int,
    swipeToDelete: () -> Unit,
    edit: Boolean,
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
                    SwipeToDismissBoxValue.StartToEnd -> swipeToEdit()
                    else -> Unit
                }
                false
            }
        ),
        enableDismissFromEndToStart = !selectionState,
        enableDismissFromStartToEnd = !selectionState,
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
            NoteSwipeContent(
                title = note,
                selected = selected,
                edit = edit,
                onSelected = onSelect,
                onValueChange = onValueChange,
                selectionState = selectionState,
                onMenuClick = onMenuClick,
                modifier = Modifier
            )
        }
    )
}