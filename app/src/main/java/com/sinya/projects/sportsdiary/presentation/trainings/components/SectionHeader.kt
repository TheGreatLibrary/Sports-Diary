package com.sinya.projects.sportsdiary.presentation.trainings.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R

@Composable
fun SectionHeader(
    title: String,
    style: TextStyle,
    rowFill: Float,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "")

    Row(
        modifier = Modifier
            .fillMaxWidth(rowFill)
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = style,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onPrimary
        )
        Icon(
            painter = painterResource(R.drawable.arrow),
            modifier = Modifier.rotate(rotation),
            contentDescription = "arrow",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}