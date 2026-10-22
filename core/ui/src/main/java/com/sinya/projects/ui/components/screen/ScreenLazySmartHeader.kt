package com.sinya.projects.ui.components.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.components.smartHeader.SmartHeader
import com.sinya.projects.ui.components.smartHeader.util.SmartHeaderManager
import com.sinya.projects.ui.components.smartHeader.util.smartHeader

@Composable
fun BoxScope.ScreenLazySmartHeader(
    modifier: Modifier = Modifier,
    list: LazyListState = rememberLazyListState(),
    arrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    navigationType: TypeTopNavigation,
    onOptionSelected: (() -> Unit)? = null,
    query: String? = null,
    onQueryChange: ((String) -> Unit)? = null,
    manager: SmartHeaderManager,
    contentList: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .widthIn(max = 550.dp),
        state = list,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = arrangement
    ) {
        contentList()
    }

    SmartHeader(
        modifier = Modifier.smartHeader(manager),
        onOptionSelected = onOptionSelected,
        navigationType = navigationType,
        query = query,
        onQueryChange = onQueryChange
    )
}