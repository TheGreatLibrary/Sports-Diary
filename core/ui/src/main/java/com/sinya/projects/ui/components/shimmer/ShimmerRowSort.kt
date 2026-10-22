package com.sinya.projects.ui.components.shimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerRowSort(roundType: Boolean = true, count: Int) {
    when(roundType) {
        true -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(
                modifier = Modifier.size(50.dp, 20.dp),
                shape = MaterialTheme.shapes.small
            )
            repeat(2) {
                ShimmerBox(
                    modifier = Modifier.size(50.dp, 30.dp),
                    shape = MaterialTheme.shapes.extraLarge
                )
            }
        }
        false -> Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBox(
                modifier = Modifier.size(100.dp, 20.dp),
                shape = MaterialTheme.shapes.small
            )
            repeat(count) {
                ShimmerBox(
                    modifier = Modifier.size(25.dp),
                    shape = MaterialTheme.shapes.extraSmall
                )
            }
        }
    }
}