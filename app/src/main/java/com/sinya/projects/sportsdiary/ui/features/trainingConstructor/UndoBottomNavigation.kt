package com.sinya.projects.sportsdiary.ui.features.trainingConstructor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.sinya.projects.sportsdiary.R

@Composable
fun UndoBottomNavigation(
    modifier: Modifier,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    isUndoing: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            then(modifier),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        UndoIconButton(
            modifier = Modifier.rotate(90f),
            onClick = onUndoClick,
            enabled = canUndo,
            isLoading = isUndoing,
            painter = painterResource(R.drawable.ic_arrow_down),
        )

        UndoIconButton(
            modifier = Modifier.rotate(90f),
            onClick = onRedoClick,
            enabled = canRedo,
            isLoading = isUndoing,
            painter = painterResource(R.drawable.ic_arrow_up),
        )
    }
}