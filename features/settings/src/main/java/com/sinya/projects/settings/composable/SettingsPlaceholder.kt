package com.sinya.projects.settings.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.shimmer.ShimmerBox
import com.sinya.projects.ui.components.shimmer.ShimmerItemCard

@Composable
internal fun SettingsPlaceholder(
    title: String,
    onBackClick: () -> Unit
) {
    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = title
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(
                modifier = Modifier.size(80.dp, 25.dp),
                shape = MaterialTheme.shapes.small
            )
            repeat(3) {
                ShimmerItemCard()
            }
        }
    }
}