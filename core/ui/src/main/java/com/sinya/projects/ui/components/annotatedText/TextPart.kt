package com.sinya.projects.ui.components.annotatedText

import androidx.compose.ui.text.SpanStyle

sealed interface TextPart {
    data class Plain(val text: String, val style: SpanStyle? = null) : TextPart
    data class Link(val text: String, val url: String) : TextPart
    data class Clickable(val text: String, val onClick: () -> Unit) : TextPart
}