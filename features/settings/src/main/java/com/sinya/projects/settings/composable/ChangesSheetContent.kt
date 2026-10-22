package com.sinya.projects.settings.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.domain.model.ChangelogState
import com.sinya.projects.settings.R
import com.sinya.projects.ui.components.CustomButton
import com.sinya.projects.ui.components.ExpandedCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChangesSheetContent(
    changeLogState: ChangelogState,
    onReload: () -> Unit,
    upToMarket: () -> Unit,
) {
    val locale = Locale.current.language

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.settings_list_of_changes),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            contentAlignment = Alignment.Center
        ) {
            when (changeLogState) {
                is ChangelogState.Loading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                is ChangelogState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = changeLogState.message, textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                is ChangelogState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(changeLogState.data.releases) { release ->
                            val localeChanges = release.locales[locale]
                                ?: release.locales["en"]
                                ?: release.locales.values.firstOrNull()

                            ChangesCard(
                                currentVersion = release.version,
                                changes = localeChanges?.changes ?: emptyList()
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            CustomButton(
                text = "Обновить",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onReload
            )
            CustomButton(
                text = "Перейти в магазин",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = upToMarket
            )
        }
    }

    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun ChangesCard(
    currentVersion: String,
    changes: List<String>
) {
    ExpandedCard(
        title = stringResource(R.string.settings_release_title, currentVersion)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (changes.isNotEmpty()) {
                changes.forEach {
                    Text(
                        text = "• $it",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else Text(
                text = stringResource(com.sinya.projects.ui.R.string.not_found_data),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
