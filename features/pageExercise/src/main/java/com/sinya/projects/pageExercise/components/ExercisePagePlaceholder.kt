package com.sinya.projects.pageExercise.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.shimmer.ShimmerBox
import com.sinya.projects.ui.components.shimmer.ShimmerDiagram

@Composable
fun ExercisePagePlaceholder(
    title: String,
    onBackClick: () -> Unit
) {
    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = ""
        )
    ) {
        ShimmerBox(
            modifier = Modifier.size(80.dp, 30.dp),
            shape = MaterialTheme.shapes.small
        )

        ShimmerDiagram()

        repeat(2) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShimmerBox(
                    modifier = Modifier.size(80.dp, 30.dp),
                    shape = MaterialTheme.shapes.small
                )
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = MaterialTheme.shapes.small
                )
            }
        }
    }
}