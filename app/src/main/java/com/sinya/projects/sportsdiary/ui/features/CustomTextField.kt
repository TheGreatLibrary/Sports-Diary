package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    leadingIcon: Painter? = null,
    onTrailingClick: () -> Unit,
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    keyboardType: KeyboardType,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }

    Column {
        BasicTextField(
            value = value,
            onValueChange = { s -> onValueChange(s) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = contentColor),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
            modifier = modifier
                .height(40.dp)
                .background(containerColor, shape)
                .border(
                    width = 1.dp,
                    color = if (isError) MaterialTheme.colorScheme.secondary else Color.Transparent,
                    shape = shape
                )
                .onFocusChanged { focused = it.isFocused },
            decorationBox = { inner ->
                TextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = inner,
                    enabled = true,
                    singleLine = true,
                    isError = isError,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interaction,
                    contentPadding = PaddingValues(8.dp),
                    container = {},
                    placeholder = {
                        placeholder?.let {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            )
                        }
                    },
                    leadingIcon = {
                        leadingIcon?.let {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = "voice",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        if (value.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = contentColor,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .clickable { onTrailingClick() },
                            )
                        }
                    }
                )
            }
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

@Composable
fun CustomTextFieldWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onTrailingClick: () -> Unit,
    keyboardType: KeyboardType,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            onTrailingClick = onTrailingClick,
            keyboardType = keyboardType,
            modifier = modifier,
            contentColor = contentColor,
            containerColor = containerColor
        )
    }
}