package com.sinya.projects.sportsdiary.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeAppTopNavigation
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon

@Composable
fun NavigationTopBar(
    type: TypeAppTopNavigation
) {
    val title = remember(type) {
        when (type) {
            is TypeAppTopNavigation.WithoutIcon -> type.title
            is TypeAppTopNavigation.WithIcon -> type.title
            is TypeAppTopNavigation.OnlyBackIcon -> null
        }
    }
    val action = remember(type) {
        when (type) {
            is TypeAppTopNavigation.WithIcon -> Pair(type.painter, type.onClick)
            else -> null
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AnimationIcon(
            onClick = type.onBackClick,
            description = "Back",
            isSelected = true,
            size = 22.dp,
            selectedContainerColor = Color.Transparent,
            icon = painterResource(R.drawable.nav_back)
        )
        title?.let {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        if (action != null)
            AnimationIcon(
                onClick = action.second,
                shape = MaterialTheme.shapes.extraLarge,
                description = "",
                isSelected = true,
                size = 22.dp,
                selectedContainerColor = Color.Transparent,
                icon = painterResource(action.first)
            )
        else {
            Spacer(modifier = Modifier.size(22.dp))
        }
    }
}