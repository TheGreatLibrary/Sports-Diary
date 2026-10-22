package com.sinya.projects.home.components.healthPager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.HealthProgressData
import com.sinya.projects.home.components.healthPager.components.PagerData
import com.sinya.projects.home.components.healthPager.components.TripleRoundProgressBar
import com.sinya.projects.ui.components.AnimationCard
import com.sinya.projects.ui.components.PagerDots
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
internal fun HealthRings(
    modifier: Modifier = Modifier,
    list: List<HealthProgressData>,
    navigateToHealth: () -> Unit
) {
    val gradients = LocalAppGradients.current
    val pagerState = rememberPagerState(pageCount = { list.size })

    AnimationCard(
        modifier = modifier,
        onClick = navigateToHealth,
        shapeCard = MaterialTheme.shapes.small,
        colorCard = gradients.basedCardGradient
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerDots(
                currentColor = MaterialTheme.colorScheme.onPrimary,
                count = list.size,
                currentPage = pagerState.currentPage,
                size = 3.dp
            )
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                PagerData(list = list, pagerState = pagerState)
                TripleRoundProgressBar(list = list)
            }
        }
    }
}