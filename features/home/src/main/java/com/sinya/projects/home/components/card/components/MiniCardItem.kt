package com.sinya.projects.home.components.card.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.ui.components.AnimationCard

@Composable
internal fun MiniCardItem(
    item: String,
    backgroundColor: Brush,
    onClick: () -> Unit
) {
    AnimationCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shapeCard = RoundedCornerShape(10.dp),
        colorCard = backgroundColor
    ) {
        Text(
            text = item,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
        )
    }
}
