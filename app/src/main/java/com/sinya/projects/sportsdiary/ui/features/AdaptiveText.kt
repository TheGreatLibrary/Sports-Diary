package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
fun AdaptiveText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    maxLines: Int = 1,
    fontSize: TextUnit,
    onTextLayout:  ((TextLayoutResult) -> Unit)?
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(fontSize = fontSize),
        maxLines = maxLines,
        overflow = TextOverflow.Clip,
        softWrap = false,
        onTextLayout = onTextLayout
    )
}