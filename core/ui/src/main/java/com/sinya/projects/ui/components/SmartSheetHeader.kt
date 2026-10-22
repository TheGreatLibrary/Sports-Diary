package com.sinya.projects.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.CategoryTextFieldItem
import com.sinya.projects.domain.model.RadioItem
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.textField.CustomTextField
import com.sinya.projects.ui.components.textField.ErrorTextField
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun BoxScope.SmartSheetHeader(
    modifier: Modifier,
    onOptionSelected: (() -> Unit)?,
    query: String?,
    category: CategoryTextFieldItem? = null,
    onQueryChange: ((String) -> Unit)?,
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .then(modifier)
            .background(LocalAppGradients.current.basedGradient)
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(0.dp)),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        category?.let { item ->
            ErrorTextField(
                modifier = Modifier
                    .heightIn(max = 40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                value = item.category,
                onValueChange = { item.onCategoryChange(it) },
                placeholder = stringResource(R.string.category_name),
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        contentDescription = "search",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                errorMessage = stringResource(R.string.category_name_is_empty),
                maxLines = 1,
                isError = item.isError,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }

        onOptionSelected?.let {
            SortedRow(
                modifier = Modifier.padding(4.dp),
                title = stringResource(R.string.sort_title),
                radioOptions = listOf(RadioItem(null, R.drawable.ic_sort, 0)),
                selectedOption = 0,
                onOptionSelected = { onOptionSelected() },
                shape = MaterialTheme.shapes.extraLarge,
            )
        }

        if (query != null && onQueryChange != null) {
            CustomTextField(
                modifier = Modifier
                    .heightIn(max = 40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                value = query,
                onValueChange = { onQueryChange(it) },
                placeholder = stringResource(R.string.put_your_title),
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        contentDescription = "search",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
