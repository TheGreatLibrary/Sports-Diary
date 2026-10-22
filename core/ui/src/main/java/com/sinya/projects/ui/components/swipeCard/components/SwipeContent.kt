package com.sinya.projects.ui.components.swipeCard.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.RoundIconButton
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun SwipeContent(
    modifier: Modifier,
    title: String,
    description: String,
    @DrawableRes icon: Int?,
    selected: Boolean,
    maxLines: Int = Int.MAX_VALUE,
    onClick1: () -> Unit,
    onSelected: () -> Unit,
    onMenuClick: () -> Unit,
    selectionState: Boolean,
) {
    val clickModifier = Modifier.clickable { onClick1() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (selectionState) {
            Checkbox(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .clip(shape = MaterialTheme.shapes.extraLarge)
                    .size(15.dp)
                    .scale(0.8f),
                checked = selected,
                onCheckedChange = { onSelected() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    brush = LocalAppGradients.current.basedCardGradient,
                    shape = RoundedCornerShape(100.dp, 0.dp, 0.dp, 100.dp)
                )
                .clip(RoundedCornerShape(100.dp, 0.dp, 0.dp, 100.dp))
                .then(clickModifier)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            icon?.let {
                RoundIconButton(
                    modifier = Modifier,
                    buttonSize = 25,
                    iconSize = 20,
                    icon = icon,
                    onClick = onClick1,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                )
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    maxLines = maxLines,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        RoundIconButton(
            modifier = Modifier
                .width(50.dp)
                .fillMaxHeight(),
            icon = R.drawable.ic_menu,
            onClick = { onMenuClick() },
            buttonSize = 36,
            iconSize = 18,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(0.dp, 100.dp, 100.dp, 0.dp)
        )
    }
}