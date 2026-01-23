package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.sinya.projects.sportsdiary.R

@Composable
fun SearchContainer(
    searchQuery: String,
    onClear: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    CustomTextField(
        value = searchQuery,
        onValueChange = onValueChanged,
        placeholder = stringResource(R.string.put_your_text),
        leadingIcon = painterResource(R.drawable.ic_search),
        onTrailingClick = onClear,
        shape = MaterialTheme.shapes.extraLarge,
        keyboardType = KeyboardType.Text,
        modifier = Modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
}
