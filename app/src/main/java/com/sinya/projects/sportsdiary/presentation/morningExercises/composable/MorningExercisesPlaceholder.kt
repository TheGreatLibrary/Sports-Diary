package com.sinya.projects.sportsdiary.presentation.morningExercises.composable

import androidx.compose.runtime.Composable
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerItemCard
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerStatisticsCard

@Composable
fun MorningExercisesPlaceholder(
    title: String,
    onBackClick: () -> Unit
) {
    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = title
        ),
    ) {
        ShimmerStatisticsCard()

        ShimmerItemCard()

        ShimmerItemCard()
    }
}