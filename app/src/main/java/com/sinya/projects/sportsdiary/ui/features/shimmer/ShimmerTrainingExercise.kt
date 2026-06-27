package com.sinya.projects.sportsdiary.ui.features.shimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerTrainingExercise(expanded: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerBox(
                modifier = Modifier.size(70.dp, 28.dp),
                shape = MaterialTheme.shapes.small
            )
            ShimmerBox(
                modifier = Modifier.size(80.dp, 25.dp),
                shape = MaterialTheme.shapes.extraLarge
            )
        }
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(((if (expanded) 4 else 1) * 50).dp),
            shape = MaterialTheme.shapes.small
        )
    }
}