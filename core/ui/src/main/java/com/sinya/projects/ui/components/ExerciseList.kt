package com.sinya.projects.ui.components

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
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.ui.R

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
            .fillMaxHeight(0.90f)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        if (filtered.isEmpty()) {
            Text(
                text = stringResource(R.string.not_found_data),
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

