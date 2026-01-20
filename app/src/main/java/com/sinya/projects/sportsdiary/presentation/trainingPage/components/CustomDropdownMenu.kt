package com.sinya.projects.sportsdiary.presentation.trainingPage.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.ui.features.HeaderInfo
import com.sinya.projects.sportsdiary.utils.getString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    items: List<TypeTraining>,
    title: String,
    selectedItem: TypeTraining,
    onOpenMenu: () -> Unit,
    onSelectedCategory: (TypeTraining) -> Unit,
    onInfoClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HeaderInfo(
            title = title,
            onInfoClick = onInfoClick
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
                onOpenMenu()
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
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = context.getString(selectedItem.name),
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
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .heightIn(max = 400.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        items.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = context.getString(item.name))
                                },
                                onClick = {
                                    onSelectedCategory(item)
                                    expanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onPlusClick() },
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = "Plus",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(7.dp)
                    )
                }
            }
        }
    }
}