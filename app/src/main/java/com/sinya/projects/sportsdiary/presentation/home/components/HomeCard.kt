package com.sinya.projects.sportsdiary.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.main.ScreenRoute
import com.sinya.projects.sportsdiary.ui.features.AnimationCard
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon
import com.sinya.projects.sportsdiary.utils.getString
import kotlin.math.acos

@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    title: String,
    colorCard: Color,
    onCardClick: (ScreenRoute) -> Unit,
    onPlusClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AnimationCard(
        modifier = modifier,
        onClick = { onCardClick(ScreenRoute.MorningExercises) },
        colorCard = colorCard
    ) {
        Column(
            modifier = Modifier.heightIn(max = 160.dp).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                onPlusClick?.let {
                    AnimationIcon(
                        onClick = it,
                        icon = painterResource(R.drawable.ic_plus),
                        size = 18.dp,
                        isSelected = true,
                        selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedContainerColor = Color.Transparent,
                    )
                }
            }
            content()
        }
    }
}