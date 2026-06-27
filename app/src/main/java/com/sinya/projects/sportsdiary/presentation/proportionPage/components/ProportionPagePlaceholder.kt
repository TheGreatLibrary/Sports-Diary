package com.sinya.projects.sportsdiary.presentation.proportionPage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerDropdown
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerRowSort

@Composable
fun ProportionPagePlaceholder(
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

        repeat(3) { ShimmerDropdown(1) }
        repeat(3) { ShimmerDropdown(2) }
    }
}