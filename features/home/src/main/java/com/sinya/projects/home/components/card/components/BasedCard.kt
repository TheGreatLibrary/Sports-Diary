package com.sinya.projects.home.components.card.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.AnimationCard
import com.sinya.projects.ui.components.RoundedIcon

@Composable
internal fun BasedCard(
    modifier: Modifier = Modifier,
    title: String,
    backgroundColor: Brush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF3E3E3E),
            Color(0xFF121212),
        )
    ),
    @DrawableRes icon: Int = R.drawable.ic_plus,
    onCardClick: () -> Unit,
    contentColor: Color  =MaterialTheme.colorScheme.onPrimaryContainer,
    onIconClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    AnimationCard(
        modifier = modifier,
        onClick = onCardClick,
        colorCard = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 160.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                RoundedIcon(
                    onClick = onIconClick,
                    painter = painterResource(icon),
                    size = 25.dp,
                    isSelected = true,
                    selectedContentColor = contentColor,
                    selectedContainerColor = Color.Transparent,
                )
            }
            content()
        }
    }
}