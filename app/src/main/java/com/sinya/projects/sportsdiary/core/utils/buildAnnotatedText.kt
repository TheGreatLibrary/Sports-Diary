package com.sinya.projects.sportsdiary.core.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.sinya.projects.sportsdiary.core.domain.model.TextPart
import com.sinya.projects.sportsdiary.ui.theme.Nunito


@Composable
fun buildAnnotatedText(
    linkStyle: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        textDecoration = TextDecoration.Underline,
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
    ),
    vararg parts: TextPart
): AnnotatedString = buildAnnotatedString {
    parts.forEach { part ->
        when (part) {
            is TextPart.Plain -> {
                val start = length
                append(part.text)
                part.style?.let {
                    addStyle(
                        style = part.style,
                        start = start,
                        end = length
                    )
                }
            }

            is TextPart.Link -> {
                val start = length
                append(part.text)
                addLink(
                    url = LinkAnnotation.Url(
                        url = part.url,
                        styles = TextLinkStyles(style = linkStyle)
                    ),
                    start = start,
                    end = length
                )
            }

            is TextPart.Clickable -> {
                val start = length
                append(part.text)
                addLink(
                    clickable = LinkAnnotation.Clickable(
                        tag = part.text,
                        styles = TextLinkStyles(style = linkStyle),
                        linkInteractionListener = { part.onClick() }
                    ),
                    start = start,
                    end = length
                )
            }
        }
    }
}

