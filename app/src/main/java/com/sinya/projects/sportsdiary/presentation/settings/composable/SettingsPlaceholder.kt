package com.sinya.projects.sportsdiary.presentation.settings.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerBox
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerItemCard

@Composable
fun SettingsPlaceholder(
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