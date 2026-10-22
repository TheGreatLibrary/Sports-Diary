package com.sinya.projects.pagetraining.form.components.card

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sinya.projects.domain.model.ExerciseItem
import com.sinya.projects.pagetraining.form.components.card.components.SwipeTrainingCardContent
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.swipeCard.components.SwipeBackground

@Composable
internal fun SwipeTrainingCard(
    modifier: Modifier = Modifier,
    item: ExerciseItem,
    onMenuClick: () -> Unit,
    onPlusClick: (Int) -> Unit,
    onMinusClick: () -> Unit,
    onEditSet: (Int, Int, String?, Boolean) -> Unit,
    onDeleteSet: (Int, Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    SwipeToDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        state = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) onMinusClick()
                false
            }
        ),
        backgroundContent = {
            SwipeBackground(iconEnd = R.drawable.morn_trash)
        },
        content = {
            SwipeTrainingCardContent(
                expanded = expanded,
                onExpanded = { expanded = it },
                item = item,
                onPlusClick = onPlusClick,
                onEditSet = onEditSet,
                onDeleteSet = onDeleteSet,
                onMenuClick = onMenuClick,
            )
        }
    )
}
