package com.sinya.projects.ui.components.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation

@Composable
fun ScreenColumn(
    modifier: Modifier = Modifier,
    arrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp, Alignment.Top),
    navigationType: TypeTopNavigation? = null,
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 550.dp)
            .padding(horizontal = 16.dp)
            then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = arrangement
    ) {
        navigationType?.let {
            NavigationTopBar(type = navigationType)
        }
        content()
    }
}