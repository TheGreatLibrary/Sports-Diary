package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.navigation.NavigationTopBar


@Composable
fun ScreenLazyColumn(
    modifier: Modifier = Modifier,
    list: LazyListState = rememberLazyListState(),
    arrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    navigationType: TypeAppTopNavigation? = null,
    content: LazyListScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = modifier
                .widthIn(max = 550.dp)
                .padding(horizontal = 16.dp),
            state = list,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = arrangement
        ) {
            navigationType?.let {
                item {
                    NavigationTopBar(type = navigationType)
                    Spacer(Modifier.height(16.dp))
                }
            }

            content()
        }
    }
}
