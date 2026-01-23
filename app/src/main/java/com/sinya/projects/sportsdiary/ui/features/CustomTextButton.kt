package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextButton(
    onClick: () -> Unit,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    TextButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 16.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}