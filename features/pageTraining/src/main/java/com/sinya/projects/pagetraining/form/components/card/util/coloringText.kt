package com.sinya.projects.pagetraining.form.components.card.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun List<Pair<String, Color>>.coloringText(): AnnotatedString {
    return buildAnnotatedString {
        forEachIndexed { index, it ->
            withStyle(SpanStyle(color = it.second)) { append(it.first) }
            if (index != size - 1) withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) { append("/") }
        }
    }
}
