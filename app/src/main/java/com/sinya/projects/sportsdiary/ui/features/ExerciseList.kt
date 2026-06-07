package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseWithMuscles

@Composable
fun ExerciseList(
    filtered: List<ExerciseWithMuscles>,
    onToggle: (Int) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        if (filtered.isEmpty()) {
            Text(
                text = stringResource(R.string.nothing_found),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary

            )
        } else {
            LazyColumn(
                state = listState,
            ) {
                items(
                    items = filtered,
                    key = { it.id },
                    contentType = { "exercise" }
                ) { ex ->
                    CustomElementCheckBox(
                        onEvent = { onToggle(ex.id) },
                        title = ex.name,
                        checked = ex.checked,
                        description = ex.musclesText,
                    )
                }
            }
        }
    }
}

