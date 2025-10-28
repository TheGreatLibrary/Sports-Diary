package com.sinya.projects.sportsdiary.ui.features.trainingConstructor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import com.sinya.projects.sportsdiary.R

@Composable
fun CustomElementTriCheck(
    triState: ToggleableState,
    onEvent: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TriStateCheckbox(
            state = triState,
            onClick = onEvent
        )
        Text(
            text = stringResource(R.string.select_all),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onEvent() }
        )
    }
}