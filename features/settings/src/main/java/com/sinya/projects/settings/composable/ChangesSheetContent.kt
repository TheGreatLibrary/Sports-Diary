package com.sinya.projects.settings.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.ChangelogState
import com.sinya.projects.settings.R
import com.sinya.projects.ui.components.ExpandedCard
import com.sinya.projects.ui.components.RoundedIcon
import com.sinya.projects.ui.components.sheet.components.SheetButtons


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChangesSheetContent(
    changeLogState: ChangelogState,
    onReload: () -> Unit,
    upToMarket: () -> Unit,
) {
    val locale = Locale.current.language

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.settings_list_of_changes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (changeLogState) {
                    is ChangelogState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(32.dp)
                        )
                    }

                    is ChangelogState.Error -> {
                        Text(
                            text = changeLogState.message,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(32.dp)
                        )
                    }

                    is ChangelogState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = 80.dp),
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
        }

        SheetButtons(
            list = listOf(
                painterResource(com.sinya.projects.ui.R.drawable.cat_change) to onReload,
                painterResource(R.drawable.set_shop) to upToMarket
            )
        )
    }
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
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else Text(
                text = stringResource(com.sinya.projects.ui.R.string.not_found_data),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
