package com.sinya.projects.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun RoundIconButton(
    modifier: Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    buttonSize: Int = 45,
    iconSize: Int = 30,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ),
    shape: CornerBasedShape = MaterialTheme.shapes.extraLarge
) {
    IconButton(
        modifier = modifier.size(buttonSize.dp),
        onClick = onClick,
        shape = shape,
        colors = colors
    ) {
        Icon(
            painter = painterResource(icon),
            modifier = Modifier.size(iconSize.dp),
            contentDescription = null
        )
    }
}