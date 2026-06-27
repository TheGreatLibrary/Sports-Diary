package com.sinya.projects.sportsdiary.presentation.statisticDiagram.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerDiagram
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerDropdown

@Composable
fun StatisticDiagramPlaceholder(
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

        repeat(2) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerDropdown()
                ShimmerDiagram()
            }
        }
    }
}