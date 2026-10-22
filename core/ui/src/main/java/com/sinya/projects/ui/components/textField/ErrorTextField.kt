package com.sinya.projects.ui.components.textField

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorTextField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    maxLines: Int = 7,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester = FocusRequester(),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        errorContainerColor = Color.Transparent
    ),
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    val interaction = remember { MutableInteractionSource() }

    Column {
        BasicTextField(
            value = value,
            onValueChange = { s -> onValueChange(s) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp),
            cursorBrush = SolidColor(contentColor),
            maxLines = maxLines,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .then(modifier)
                .border(
                    width = 1.dp,
                    color = if (isError) MaterialTheme.colorScheme.secondary else Color.Transparent,
                    shape = shape
                ),
            decorationBox = { inner ->
                TextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = inner,
                    enabled = true,
                    isError = isError,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    colors = colors,
                    contentPadding = PaddingValues(8.dp),
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    placeholder = {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 12.sp
                            )
                        )
                    },
                    interactionSource = interaction,
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            )
        )
        errorMessage?.let {
            Text(
                text = if (isError) errorMessage else "",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}