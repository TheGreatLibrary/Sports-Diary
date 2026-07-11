package com.sinya.projects.sportsdiary.ui.features.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.ui.theme.AppColors.Blue700


@Composable
fun ProgressBarMuscles() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF4E3AA8),
                            Color(0xFF3A48CE),
                            Blue700,
                            Color(0xFF1B5E20),
                            Color(0xFFFFD54F),
                            Color(0xFFFF9800),
                            Color(0xFFB71C1C)
                        )
                    ),
                    MaterialTheme.shapes.extraLarge
                )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("0", "10", "20", "40", "60", "80", "100%").forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.wrapContentWidth(),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}