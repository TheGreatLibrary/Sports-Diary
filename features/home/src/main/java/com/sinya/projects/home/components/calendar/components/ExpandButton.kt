package com.sinya.projects.home.components.calendar.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.R

@Composable
internal fun ExpandButton(
    expanded: Boolean,
    onClick: () -> Unit
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "arrowRotation"
    )

    Box(
        modifier = Modifier
            .width(70.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, 100.dp, 100.dp))
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(0.dp, 0.dp, 100.dp, 100.dp)
            )
            .padding(bottom = 7.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.TopCenter
    ) {
        Icon(
            contentDescription = null,
            painter = painterResource(R.drawable.ic_arrow),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(19.dp).rotate(arrowRotation)
        )
    }
}