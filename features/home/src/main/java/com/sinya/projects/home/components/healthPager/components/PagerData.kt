package com.sinya.projects.home.components.healthPager.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.domain.model.HealthProgressData

@Composable
internal fun PagerData(
    list: List<HealthProgressData>,
    pagerState: PagerState
) {
    HorizontalPager(
        modifier = Modifier.clip(MaterialTheme.shapes.extraLarge),
        state = pagerState,
        userScrollEnabled = true
    ) { page ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(list[page].icon),
                contentDescription = null,
                tint = Color(list[page].color.toArgb())
            )
            Text(
                text = list[page].count.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 12.sp,
                color = Color(list[page].color.toArgb())
            )
        }
    }
}