package com.sinya.projects.sportsdiary.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.TypeBottomNavigation
import com.sinya.projects.ui.components.RoundIconButton
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
internal fun NavigationBottomBar(
    currentRoute: String,
    list: List<TypeBottomNavigation>,
) {
    val gradients = LocalAppGradients.current

    Row(
        modifier = Modifier
            .padding(bottom = 15.dp)
            .background(
                brush = gradients.basedCardGradient65,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 5.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        list.forEach {
            RoundIconButton(
                modifier = Modifier,
                icon = it.icon,
                onClick = { it.onClick() },
                buttonSize = 45,
                iconSize = 24,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor =
                        if (currentRoute != it.key) MaterialTheme.colorScheme.surfaceContainerLowest
                        else MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        }
    }
}

