package com.sinya.projects.ui.components.trainingConstructor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.R
import kotlin.math.abs

@Composable
fun DeltaHint(delta: Float?) {
    if (delta == null || delta == 0f) return

    val color =  if (delta > 0f) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = if (delta > 0f) painterResource(R.drawable.ic_arrow_up) else painterResource(R.drawable.ic_arrow_down),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "${abs(delta)}",
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}