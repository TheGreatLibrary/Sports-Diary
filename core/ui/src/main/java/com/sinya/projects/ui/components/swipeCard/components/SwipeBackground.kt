package com.sinya.projects.ui.components.swipeCard.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SwipeBackground(
    modifier: Modifier = Modifier,
    @DrawableRes iconStart: Int? = null,
    @DrawableRes iconEnd: Int,
    colorStart: Color = MaterialTheme.colorScheme.onPrimary,
    colorEnd: Color = MaterialTheme.colorScheme.secondary,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        when(iconStart) {
            null -> Spacer(Modifier.size(24.dp))
            else -> Icon(
                painter = painterResource(iconStart),
                contentDescription = null,
                tint = colorStart,
                modifier = Modifier.size(24.dp)
            )
        }
        Icon(
            painter = painterResource(iconEnd),
            contentDescription = "Delete",
            tint = colorEnd,
            modifier = Modifier.size(24.dp)
        )
    }
}