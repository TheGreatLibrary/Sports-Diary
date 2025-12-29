package com.sinya.projects.sportsdiary.presentation.home.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.ui.features.LegendDot

@Composable
fun Legend() {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        LegendDot(
            text = stringResource(R.string.morning_exercises),
            color = MaterialTheme.colorScheme.primary
        )
        LegendDot(
            text = stringResource(R.string.training),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
