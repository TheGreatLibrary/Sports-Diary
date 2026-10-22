package com.sinya.projects.ui.components.dropdown

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.HeaderWithInfo
import com.sinya.projects.ui.components.RoundIconButton
import com.sinya.projects.ui.theme.LocalAppGradients

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  <T> DropdownCheckboxMenu(
    items: List<T>,
    title: String,
    onInfoClick: (() -> Unit)?,
    selectedItems: List<T>,
    nameObject: (List<T>) -> String,
    expanded: Boolean,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onExpanded: (Boolean) -> Unit,
    onPlusClick: (() -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    val gradients = LocalAppGradients.current
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "")

    Column(verticalArrangement = Arrangement.spacedBy(4.dp))
    {
        HeaderWithInfo(
            title = title,
            alignment = alignment,
            onInfoClick = onInfoClick
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                onExpanded(!expanded)
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .menuAnchor()
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = when(onPlusClick) {
                                    null -> MaterialTheme.shapes.extraLarge
                                    else -> RoundedCornerShape(25.dp, 0.dp, 0.dp, 25.dp)
                                }
                            )
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = nameObject(selectedItems),
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow),
                            modifier = Modifier.rotate(rotation),
                            contentDescription = "arrow",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onExpanded(false) },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .heightIn(max = 400.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        items.forEach { item ->
                            content(item)
                        }
                    }
                }

                onPlusClick?.let {
                    RoundIconButton(
                        modifier = Modifier.width(45.dp),
                        icon = R.drawable.ic_plus,
                        onClick = onPlusClick,
                        buttonSize = 36,
                        iconSize = 24,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(0.dp, 25.dp, 25.dp, 0.dp)
                    )
                }
            }
        }
    }
}