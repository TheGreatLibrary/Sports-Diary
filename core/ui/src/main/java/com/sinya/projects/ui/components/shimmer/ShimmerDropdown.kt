package com.sinya.projects.ui.components.shimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerDropdown(count: Int = 1) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ShimmerRowSort(false, 1)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(count) {
                ShimmerBox(
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = MaterialTheme.shapes.small
                )
            }
        }
    }
}