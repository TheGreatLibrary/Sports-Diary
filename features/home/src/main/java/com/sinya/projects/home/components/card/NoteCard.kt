package com.sinya.projects.home.components.card

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.home.R
import com.sinya.projects.home.components.card.components.BasedCard
import com.sinya.projects.ui.components.textField.CustomTextField
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
internal fun NoteCard(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onIconClick: () -> Unit,
    contentColor: Color
) {
    val gradients = LocalAppGradients.current
    val focusRequester by remember { mutableStateOf(FocusRequester()) }

    BasedCard(
        modifier = Modifier.heightIn(max = 350.dp),
        title = stringResource(R.string.home_note_of_morning),
        contentColor = contentColor,
        icon = com.sinya.projects.ui.R.drawable.nav_save,
        backgroundColor = gradients.basedCardGradient,
        onCardClick = { focusRequester.requestFocus() },
        onIconClick = onIconClick
    ) {
        CustomTextField(
            value = value,
            placeholder = placeholder,
            maxLines = 7,
            focusRequester = focusRequester,
            onValueChange = { s -> onValueChange(s) },
            contentColor = contentColor
        )
    }
}