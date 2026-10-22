package com.sinya.projects.ui.components.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.RoundIconButton

@Composable
fun NavigationTopBar(
    type: TypeTopNavigation
) {
    val title = remember(type) {
        when (type) {
            is TypeTopNavigation.WithoutIcon -> type.title
            is TypeTopNavigation.WithIcon -> type.title
            is TypeTopNavigation.OnlyBackIcon -> null
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
        RoundIconButton(
            modifier = Modifier,
            buttonSize = 32,
            iconSize = 20,
            icon = R.drawable.nav_back,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            onClick = type.onBackClick
        )
        title?.let {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        when (type) {
            is TypeTopNavigation.WithIcon -> {
                RoundIconButton(
                    modifier = Modifier,
                    buttonSize = 32,
                    iconSize = 20,
                    icon = type.painter,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = type.onClick
                )
            }
            else -> Spacer(modifier = Modifier.size(26.dp))
        }
    }
}