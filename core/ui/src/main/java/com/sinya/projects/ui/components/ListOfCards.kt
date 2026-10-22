package com.sinya.projects.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.domain.model.CardItem
import com.sinya.projects.ui.theme.AppGradients
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun ListOfCards(
    list: List<CardItem> = listOf(),
    gradients: AppGradients = LocalAppGradients.current
) {
    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(25.dp))
            .background(
                brush = gradients.basedCardGradient,
                shape = RoundedCornerShape(25.dp)
            )
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        list.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        item.onClick()
                    }
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .size(25.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = MaterialTheme.shapes.extraLarge
                            )
                            .padding(5.dp),
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    item.description?.let {
                        Text(
                            text = "•  $it",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                item.switchState?.let {
                    Switch(
                        modifier = Modifier
                            .size(43.dp, 23.dp)
                            .scale(0.65f),
                        checked = it,
                        onCheckedChange = null,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedTrackColor = MaterialTheme.colorScheme.inverseOnSurface,
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}