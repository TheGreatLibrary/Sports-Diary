package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.presentation.trainingPage.modalSheetCategory.ExerciseUi

@Composable
fun ExerciseList(
    filtered: List<ExerciseUi>,
    onToggle: (Int) -> Unit
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
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            items(
                items = filtered,
                key = { it.id }
            ) { ex ->
                CustomElementCheckBox(
                    onEvent = { onToggle(ex.id) },
                    ex = ex
                )
            }
        }
    }
}

