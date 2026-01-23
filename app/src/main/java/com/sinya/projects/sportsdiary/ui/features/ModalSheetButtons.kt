package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ModalSheetButtons(
    cancelOnClick: () -> Unit,
    cancelText: String,
    doneOnClick: () -> Unit,
    doneText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        CustomTextButton(
            onClick = cancelOnClick,
            text = cancelText
        )
        CustomButton(
            text = doneText,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            onClick = doneOnClick
        )
    }
}