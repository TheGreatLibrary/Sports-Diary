package com.sinya.projects.exercises.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.shimmer.ShimmerBox
import com.sinya.projects.ui.components.shimmer.ShimmerItemCard
import com.sinya.projects.ui.components.shimmer.ShimmerRowSort

@Composable
internal fun ExercisesPlaceholder(
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ShimmerRowSort(false, 1)

            ShimmerBox(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = MaterialTheme.shapes.extraLarge
            )
        }

        Spacer(Modifier)

        repeat(7) {
            ShimmerItemCard()
        }
    }
}