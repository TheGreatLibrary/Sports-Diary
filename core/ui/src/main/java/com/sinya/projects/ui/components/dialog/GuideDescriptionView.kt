package com.sinya.projects.ui.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.theme.AppColors


@Composable
fun GuideDescriptionView(
    title: String,
    description: String?,
    image: Painter?
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onPrimary
    )
    image?.let {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .background(
                    color = AppColors.Black600,
                    shape = MaterialTheme.shapes.extraLarge
                )
                .clip(MaterialTheme.shapes.extraLarge)

        )
    }
    description?.let {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}