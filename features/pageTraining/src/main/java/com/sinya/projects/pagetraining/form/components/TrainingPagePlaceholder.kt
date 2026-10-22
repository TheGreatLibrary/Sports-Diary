package com.sinya.projects.pagetraining.form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.shimmer.ShimmerButton
import com.sinya.projects.ui.components.shimmer.ShimmerDropdown
import com.sinya.projects.ui.components.shimmer.ShimmerRowSort
import com.sinya.projects.ui.components.shimmer.ShimmerTrainingExercise

@Composable
internal fun TrainingPagePlaceholder(
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

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) { ShimmerRowSort(false, 1) }
        ShimmerDropdown()
        Spacer(Modifier.height(20.dp))

        repeat(4) { ShimmerTrainingExercise() }

        ShimmerButton()
    }
}