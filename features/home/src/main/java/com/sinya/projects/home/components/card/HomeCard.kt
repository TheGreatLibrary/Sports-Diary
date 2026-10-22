package com.sinya.projects.home.components.card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.home.components.card.components.BasedCard
import com.sinya.projects.home.components.card.components.MiniCardItem
import com.sinya.projects.ui.R
import kotlinx.coroutines.launch

@Composable
internal fun <T> HomeCard(
    modifier: Modifier = Modifier,
    list: List<T> = emptyList(),
    title: String,
    description: String,
    descriptionAlign: TextAlign = TextAlign.Start,
    onItemClick: (T) -> Unit,
    getText: @Composable (T) -> String,
    backgroundColor: Brush,
    backgroundItemColor: Brush,
    @DrawableRes icon: Int = R.drawable.ic_plus,
    onCardClick: () -> Unit,
    onIconClick: () -> Unit,
) {
    BasedCard(
        modifier = modifier,
        title = title,
        backgroundColor = backgroundColor,
        icon = icon,
        onCardClick = onCardClick,
        onIconClick = onIconClick
    ) {
        when {
            list.isNotEmpty() -> {
                val listState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(list.size) {
                    coroutineScope.launch {
                        listState.scrollToItem(list.lastIndex)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(list) { item ->
                        MiniCardItem(
                            item = getText(item),
                            onClick = { onItemClick(item) },
                            backgroundColor = backgroundItemColor
                        )
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = description,
                        textAlign = descriptionAlign,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}