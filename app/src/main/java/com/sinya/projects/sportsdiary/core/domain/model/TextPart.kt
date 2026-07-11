package com.sinya.projects.sportsdiary.core.domain.model

import androidx.compose.ui.text.SpanStyle

sealed interface TextPart {
    data class Plain(val text: String, val style: SpanStyle? = null) : TextPart
    data class Link(val text: String, val url: String) : TextPart
    data class Clickable(val text: String, val onClick: () -> Unit) : TextPart
}