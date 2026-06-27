package com.sinya.projects.sportsdiary.presentation.error

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn

@Composable
fun ErrorScreen(
    onBackClick: () -> Unit,
    errorMessage: String
) {
    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = stringResource(R.string.error_title)
        )
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}