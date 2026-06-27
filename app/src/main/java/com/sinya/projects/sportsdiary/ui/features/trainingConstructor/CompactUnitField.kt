package com.sinya.projects.sportsdiary.ui.features.trainingConstructor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactUnitField(
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    delta: Float?,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }
    var focused by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            val filtered = filterInput(newValue.text, keyboardType)
            if (filtered != textFieldValue.text) {
                textFieldValue = TextFieldValue(filtered, selection = TextRange(filtered.length))
            }
            onValueChange(filtered)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
        modifier = modifier
            .height(40.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
            .onFocusChanged {
                focused = it.isFocused
                if (it.isFocused) {
                    textFieldValue = textFieldValue.copy(
                        selection = TextRange(textFieldValue.text.length)
                    )
                }
            },
        decorationBox = { inner ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = inner,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interaction,
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                container = {},
                trailingIcon = {
                    AnimatedVisibility(!focused) {
                        Row(
                            modifier = Modifier.padding(end = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DeltaHint(delta)
                            Text(
                                text = unit,
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            )
        }
    )
}

private fun filterInput(s: String, keyboardType: KeyboardType): String = when (keyboardType) {
    KeyboardType.Decimal -> {
        val norm = s.replace(',', '.')
        val filtered = buildString {
            var dotSeen = false
            for (ch in norm) {
                when {
                    ch.isDigit() -> append(ch)
                    ch == '.' && !dotSeen -> { append(ch); dotSeen = true }
                    else -> Unit
                }
            }
        }
        when {
            filtered.isEmpty() -> filtered
            filtered == "0" -> filtered
            filtered.startsWith("0.") -> filtered
            filtered.startsWith("0") -> filtered.trimStart('0').ifEmpty { "0" }
            else -> filtered
        }
    }
    KeyboardType.Number -> {
        val filtered = s.filter { it.isDigit() }
        if (filtered.startsWith("0") && filtered.length > 1)
            filtered.trimStart('0').ifEmpty { "0" }
        else filtered
    }
    else -> s
}