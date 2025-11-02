package com.sinya.projects.sportsdiary.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.ui.features.AnimationIcon

@Composable
fun NavigationBottomBar(
    currentRoute: String?,
    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onStatClick: () -> Unit = {},
    onSetClick: () -> Unit = {},
    onPlusClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    shape = MaterialTheme.shapes.extraLarge
                )
                .padding(horizontal = 15.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 74.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                AnimationIcon(
                    onClick = onHomeClick,
                    description = ScreenRoute.Home.route,
                    isSelected = currentRoute == ScreenRoute.Home.route,
                    selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = painterResource(R.drawable.nav_home)
                )
                AnimationIcon(
                    onClick = onMenuClick,
                    description = ScreenRoute.Menu.route,
                    isSelected = currentRoute == ScreenRoute.Menu.route,
                    selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = painterResource(R.drawable.nav_menu)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                AnimationIcon(
                    onClick = onStatClick,
                    description = ScreenRoute.Statistic.route,
                    isSelected = currentRoute == ScreenRoute.Statistic.route,
                    selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = painterResource(R.drawable.nav_stat)
                )
                AnimationIcon(
                    onClick = onSetClick,
                    description = ScreenRoute.Settings.route,
                    isSelected = currentRoute == ScreenRoute.Settings.route,
                    selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = painterResource(R.drawable.nav_set)
                )
            }
        }
        Box(
            modifier = Modifier.padding(bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimationIcon(
                onClick = onPlusClick,
                description = "Plus",
                icon = painterResource(R.drawable.ic_plus),
                size = 60.dp,
                isSelected = true,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedContainerColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}