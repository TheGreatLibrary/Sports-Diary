package com.sinya.projects.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.components.shimmer.ShimmerBox

@Composable
internal fun HomePlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = MaterialTheme.shapes.medium
            )

            Box(
                contentAlignment = Alignment.Center
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(35.dp),
                    shape = MaterialTheme.shapes.medium
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerBox(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = MaterialTheme.shapes.small
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                repeat(2) {
                    ShimmerBox(
                        modifier = Modifier.weight(1f).height(120.dp),
                        shape = MaterialTheme.shapes.small
                    )
                }
            }
        }
    }
}