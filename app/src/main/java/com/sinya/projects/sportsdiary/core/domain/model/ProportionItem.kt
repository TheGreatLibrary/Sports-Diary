package com.sinya.projects.sportsdiary.core.domain.model

import androidx.compose.ui.focus.FocusRequester

data class ProportionItem(
    val id: Int?,
    val title: String,
    val date: String,
    val focusRequesters: List<FocusRequester>,
    val items: List<ProportionRow> = emptyList(),
)