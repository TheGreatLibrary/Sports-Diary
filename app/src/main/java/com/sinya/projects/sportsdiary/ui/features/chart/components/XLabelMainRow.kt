package com.sinya.projects.sportsdiary.ui.features.chart.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartState

@Composable
fun XLabelMainRow(
    state: ChartState,
    points: List<Pair<String, Int>>,
    scrollState: ScrollState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = state.contentPadding)
            .horizontalScroll(scrollState)
    ) {
        points.forEach { (commonPart, count) ->
            Box(modifier = Modifier.width(state.xStep*count)) {
                Text(
                    text = commonPart,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}