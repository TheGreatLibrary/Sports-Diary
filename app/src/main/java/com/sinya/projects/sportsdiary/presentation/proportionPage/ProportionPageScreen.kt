package com.sinya.projects.sportsdiary.presentation.proportionPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.enums.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.main.NavigationTopBar
import com.sinya.projects.sportsdiary.presentation.error.ErrorScreen
import com.sinya.projects.sportsdiary.presentation.placeholder.PlaceholderScreen
import com.sinya.projects.sportsdiary.presentation.proportionPage.components.buildRowsFromKeys
import com.sinya.projects.sportsdiary.ui.features.HeaderInfo
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDescriptionView
import com.sinya.projects.sportsdiary.ui.features.dialog.GuideDialog
import com.sinya.projects.sportsdiary.ui.features.trainingConstructor.CompactUnitField
import com.sinya.projects.sportsdiary.utils.deltaFloat
import com.sinya.projects.sportsdiary.utils.getDrawableId
import com.sinya.projects.sportsdiary.utils.getString

@Composable
fun ProportionPageScreen(
    state: ProportionPageUiState,
    onEvent: (ProportionPageUiEvent) -> Unit,
    onInfoClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    when (state) {
        is ProportionPageUiState.Loading -> PlaceholderScreen()
        is ProportionPageUiState.Success -> ProportionPageView(
            title = state.item.title,
            onBackClick = onBackClick,
            proportion = state.item,
            dialogContent = state.dialogContent,
            onEvent = onEvent
        )
        is ProportionPageUiState.Error -> ErrorScreen(state.message)
    }
}

@Composable
private fun ProportionPageView(
    title: String,
    onBackClick: () -> Unit,
    proportion: ProportionItem,
    dialogContent: ProportionDialogContent?,
    onEvent: (ProportionPageUiEvent) -> Unit,
) {
    val context = LocalContext.current
    val rows = remember(proportion) { buildRowsFromKeys(proportion.items) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NavigationTopBar(
            type = TypeAppTopNavigation.WithIcon(
                onBackClick = onBackClick,
                title = stringResource(R.string.proportion_number, title),
                painter = R.drawable.nav_save,
                onClick = {
                    onEvent(ProportionPageUiEvent.Save)
                    onBackClick()
                }
            )
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(rows.size) { idx ->
                when (val row = rows[idx]) {
                    is RowItem.Single -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HeaderInfo(
                                title = context.getString(row.headerKey),
                                onInfoClick = { onEvent(ProportionPageUiEvent.OpenDialog(row.item.id)) }
                            )
                            CompactUnitField(
                                value = row.item.value,
                                onValueChange = { str ->
                                    onEvent(
                                        ProportionPageUiEvent.OnChangeValue(
                                            row.item.id,
                                            str
                                        )
                                    )
                                },
                                unit = context.getString(row.item.unitMeasure),
                                modifier = Modifier.fillMaxWidth(),
                                delta = deltaFloat(row.item.value, null),
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                    }

                    is RowItem.PairRow -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HeaderInfo(
                                title = context.getString(row.headerKey),
                                onInfoClick = { onEvent(ProportionPageUiEvent.OpenDialog(row.common?.id ?: row.right.id)) }
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CompactUnitField(
                                    value = row.left.value,
                                    onValueChange = { str ->
                                        onEvent(
                                            ProportionPageUiEvent.OnChangeValue(
                                                row.left.id,
                                                str
                                            )
                                        )
                                    },
                                    unit = context.getString(row.left.unitMeasure),
                                    modifier = Modifier.weight(1f),
                                    delta = deltaFloat(row.left.value, null),
                                    keyboardType = KeyboardType.Decimal
                                )
                                CompactUnitField(
                                    value = row.right.value,
                                    onValueChange = { str ->
                                        onEvent(
                                            ProportionPageUiEvent.OnChangeValue(
                                                row.right.id,
                                                str
                                            )
                                        )
                                    },
                                    unit = context.getString(row.right.unitMeasure),
                                    modifier = Modifier.weight(1f),
                                    delta = deltaFloat(row.right.value, null),
                                    keyboardType = KeyboardType.Decimal
                                )
                            }
                        }
                    }
                }
            }
        }

        dialogContent?.let {
            GuideDialog(
                onDismiss = {
                    onEvent(ProportionPageUiEvent.OpenDialog(null))
                },
                content = {
                    GuideDescriptionView(
                        title = it.name,
                        description = it.description,
                        image = if (!it.icon.isNullOrEmpty()) painterResource(context.getDrawableId(it.icon)) else null
                    )
                }
            )
        }
    }
}
