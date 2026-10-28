package com.sinya.projects.pageExercise.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.sinya.projects.domain.model.RadioItem
import com.sinya.projects.pageExercise.ExerciseMediaItem
import com.sinya.projects.pageExercise.R
import com.sinya.projects.ui.R.drawable
import com.sinya.projects.ui.components.ChartSortedRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExerciseMediaCarousel(
    mediaItems: List<ExerciseMediaItem>,
    modifier: Modifier = Modifier
) {
    val validItems = remember(mediaItems) {
        mutableStateListOf<ExerciseMediaItem>().apply { addAll(mediaItems) }
    }

    if (validItems.isEmpty()) {
        EmptyMediaPlaceholder(modifier = modifier)
        return
    }

    val pagerState = rememberPagerState(pageCount = { validItems.size })
    val coroutineScope = rememberCoroutineScope()

    val radioOptions = validItems.map { mediaItem ->
        val title = when (mediaItem) {
            is ExerciseMediaItem.Gif -> stringResource(R.string.animation)
            is ExerciseMediaItem.PhotoSlideshow -> stringResource(R.string.photo)
            is ExerciseMediaItem.CustomPhoto -> stringResource(R.string.my_photo)
        }
        RadioItem(
            text = title,
            icon = null,
            value = mediaItem
        )
    }

    val currentSelectedItem = validItems.getOrNull(pagerState.currentPage) ?: validItems.first()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (validItems.size > 1) {
            ChartSortedRow(
                radioOptions = radioOptions,
                selectedOption = currentSelectedItem, // Если selectedOption ждет String, передай сюда radioOptions[pagerState.currentPage].title
                onOptionSelected = { selectedMediaItem ->
                    val targetIndex = validItems.indexOf(selectedMediaItem)
                    if (targetIndex != -1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(targetIndex)
                        }
                    }
                }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(1.dp)
        ) { page ->
            val item = validItems.getOrNull(page) ?: return@HorizontalPager

            when (item) {
                is ExerciseMediaItem.CustomPhoto -> {
                    SingleImageSlide(
                        url = item.uri,
                        contentScale = ContentScale.Crop,
                        onError = { validItems.remove(item) }
                    )
                }

                is ExerciseMediaItem.PhotoSlideshow -> {
                    SmoothTwoFrameSlideshow(
                        firstUrl = item.firstUrl,
                        secondUrl = item.secondUrl,
                        onBothFailed = { validItems.remove(item) }
                    )
                }

                is ExerciseMediaItem.Gif -> {
                    SingleImageSlide(
                        url = item.url,
                        contentScale = ContentScale.Fit,
                        backgroundColor = Color.White,
                        onError = { validItems.remove(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleImageSlide(
    url: String,
    contentScale: ContentScale,
    backgroundColor: Color = Color.Transparent,
    onError: () -> Unit
) {
    val context = LocalContext.current
    var isLoaded by remember(url) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (!isLoaded) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
        }

        AsyncImage(
            model = remember(url) {
                ImageRequest.Builder(context)
                    .data(url)
                    .size(Size.ORIGINAL)
                    .build()
            },
            contentDescription = null,
            contentScale = contentScale,
            onSuccess = { isLoaded = true },
            onError = { onError() },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun EmptyMediaPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(drawable.cat_change),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.no_photo),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SmoothTwoFrameSlideshow(
    firstUrl: String,
    secondUrl: String,
    onBothFailed: () -> Unit
) {
    val context = LocalContext.current
    var isFirstLoaded by remember(firstUrl) { mutableStateOf(false) }
    var isSecondLoaded by remember(secondUrl) { mutableStateOf(false) }
    var showSecondFrame by remember { mutableStateOf(false) }

    LaunchedEffect(isFirstLoaded, isSecondLoaded) {
        if (isFirstLoaded && isSecondLoaded) {
            while (true) {
                delay(900)
                showSecondFrame = !showSecondFrame
            }
        }
    }

    val secondFrameAlpha by animateFloatAsState(
        targetValue = if (showSecondFrame && isSecondLoaded) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "FrameAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center
    ) {
        if (!isFirstLoaded) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
        }

        AsyncImage(
            model = remember(firstUrl) {
                ImageRequest.Builder(context).data(firstUrl).size(Size.ORIGINAL).build()
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { isFirstLoaded = true },
            onError = {
                onBothFailed()
            },
            modifier = Modifier.fillMaxSize()
        )

        AsyncImage(
            model = remember(secondUrl) {
                ImageRequest.Builder(context).data(secondUrl).size(Size.ORIGINAL).build()
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { isSecondLoaded = true },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = secondFrameAlpha }
        )
    }
}