package com.sinya.projects.pagetraining

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.pagetraining.analyze.TrainingAnalyze
import com.sinya.projects.pagetraining.form.TrainingForm
import com.sinya.projects.ui.components.PagerDots


@Composable
fun TrainingPageScreen(
    id: Int?,
    key: Int,
    date: String?,
    onBackClick: () -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = key,
        pageCount = { 2 }
    )
    var currentTrainingKey by remember { mutableStateOf(id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> TrainingAnalyze(
                    id = currentTrainingKey,
                    onBackClick = onBackClick,
                )
                1 -> TrainingForm(
                    id = id,
                    date = date,
                    onBackClick = onBackClick
                )
            }
        }
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            PagerDots(
                currentPage = pagerState.currentPage,
                count = pagerState.pageCount,
                size = 5.dp
            )
        }
    }
}