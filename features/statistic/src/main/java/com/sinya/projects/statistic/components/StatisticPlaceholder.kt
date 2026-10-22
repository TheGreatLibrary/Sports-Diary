package com.sinya.projects.statistic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.shimmer.ShimmerBox
import com.sinya.projects.ui.components.shimmer.ShimmerDiagram
import com.sinya.projects.ui.components.shimmer.ShimmerItemCard
import com.sinya.projects.ui.components.shimmer.ShimmerStatisticsCard

@Composable
internal fun StatisticPlaceholder(
    title: String,
    onBackClick: () -> Unit
) {
    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = title
        )
    ) {
        ShimmerStatisticsCard()

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(3) {
                    ShimmerBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                }
            }
            ShimmerDiagram()
        }

        ShimmerItemCard()
    }
}