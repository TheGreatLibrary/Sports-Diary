package com.sinya.projects.sportsdiary.presentation.categories.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn
import com.sinya.projects.sportsdiary.ui.features.shimmer.ShimmerBox

@Composable
fun CategoriesPlaceholder(
    title: String,
    onBackClick: () -> Unit
) {
    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = title
        )
    ) {
        repeat(7) {
            ShimmerBox(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = MaterialTheme.shapes.small
            )
        }
    }
}