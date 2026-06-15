package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimationIcon(
    onClick: () -> Unit,
    description: String? = null,
    painter: Painter,
    isSelected: Boolean = true,
    size: Dp = 35.dp,
    shape: Shape =  MaterialTheme.shapes.extraLarge,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContainerColor: Color = Color.Transparent,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isSelected) selectedContainerColor else unselectedContainerColor,
            contentColor = if (isSelected) selectedContentColor else unselectedContentColor
        ),
        modifier = Modifier.size(size),
        shape = shape
    ) {
        Icon(
            painter = painter,
            contentDescription = description
        )
    }
}