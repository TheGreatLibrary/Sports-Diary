package com.sinya.projects.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.ui.R

@Composable
fun HeaderWithInfo(
    title: String,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onInfoClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 5.dp, alignment
        )
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        onInfoClick?.let {
            RoundedIcon(
                onClick = onInfoClick,
                painter = painterResource(R.drawable.ic_info),
                isSelected = true,
                size = 25.dp,
                selectedContainerColor = Color.Transparent,
            )
        }
    }
}