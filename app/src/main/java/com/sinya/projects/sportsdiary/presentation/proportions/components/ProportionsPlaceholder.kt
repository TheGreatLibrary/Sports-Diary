package com.sinya.projects.sportsdiary.presentation.proportions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerItemCard
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerRowSort


@Composable
fun ProportionsPlaceholder(
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

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            repeat(2) { ShimmerRowSort(true, 2) }
        }

        repeat(7) {
            ShimmerItemCard()
        }
    }
}

