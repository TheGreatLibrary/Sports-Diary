package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R

@Composable
fun SwipeCardBackground(
    modifier: Modifier = Modifier,
    painter1: Painter? = null,
    color1: Color = MaterialTheme.colorScheme.primary,
    painter2: Painter = painterResource(R.drawable.morn_trash),
    color2: Color = MaterialTheme.colorScheme.secondary
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if(painter1!=null) {
            Icon(
                painter = painter1,
                contentDescription = null,
                tint = color1,
                modifier = Modifier.size(22.dp)
            )
        }
        else Spacer(Modifier.size(22.dp))
        Icon(
            painter2,
            contentDescription = "Delete",
            tint = color2,
            modifier = Modifier.size(22.dp)
        )
    }
}
