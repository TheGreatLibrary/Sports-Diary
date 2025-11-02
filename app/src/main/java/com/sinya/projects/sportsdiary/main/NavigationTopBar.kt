package com.sinya.projects.sportsdiary.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon

@Composable
fun NavigationTopBar(
    title: String,
    isVisibleBack: Boolean = true,
    onBackClick: () -> Unit = {},
    isVisibleSave: Boolean = false,
    secondaryIcon: Painter = painterResource(R.drawable.nav_save),
    onSecondaryClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isVisibleBack) AnimationIcon(
            onClick = onBackClick,
            description = "Back",
            isSelected = true,
            selectedContainerColor = Color.Transparent,
            icon = painterResource(R.drawable.nav_back)
        ) else Spacer(Modifier.size(30.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
        )
        if (isVisibleSave) AnimationIcon(
            onClick = onSecondaryClick,
            description = "Save",
            isSelected = true,
            selectedContainerColor = Color.Transparent,
            icon = secondaryIcon
        ) else Spacer(Modifier.size(30.dp))
    }
}