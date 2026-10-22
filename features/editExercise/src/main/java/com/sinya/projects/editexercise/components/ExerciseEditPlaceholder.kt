package com.sinya.projects.editexercise.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.shimmer.ShimmerBox
import com.sinya.projects.ui.components.shimmer.ShimmerButton
import com.sinya.projects.ui.components.shimmer.ShimmerDropdown

@Composable
fun ExerciseEditPlaceholder(
    title: String,
    onBackClick: () -> Unit
) {
    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = title
        ),
        arrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier)

        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            shape = MaterialTheme.shapes.extraLarge
        )

        Spacer(Modifier)

        repeat(2) {
            ShimmerBox(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = MaterialTheme.shapes.small
            )
        }

        repeat(4) { ShimmerDropdown() }

        ShimmerBox(
            modifier = Modifier.size(200.dp, 50.dp),
            shape = MaterialTheme.shapes.small
        )

        ShimmerButton()
    }
}