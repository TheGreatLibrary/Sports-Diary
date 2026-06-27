package com.sinya.projects.sportsdiary.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.core.domain.enums.TypeBottomNavigation
import com.sinya.projects.sportsdiary.ui.features.RoundedIcon

@Composable
fun NavigationBottomBar(
    currentRoute: String?,
    navigateTo: (ScreenRoute, ScreenRoute?) -> Unit
) {
    val firstPart = remember { TypeBottomNavigation.getFirstPartList() }
    val secondPart = remember { TypeBottomNavigation.getSecondPartList() }
    val plusBtn = remember { TypeBottomNavigation.getPlus() }

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
            SubNavRow(
                navList = firstPart,
                currentRoute = currentRoute,
                navigateTo = { route -> navigateTo(route, null) }
            )
            SubNavRow(
                navList = secondPart,
                currentRoute = currentRoute,
                navigateTo = { route -> navigateTo(route, null) }
            )
        }
        Box(
            modifier = Modifier.padding(bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            RoundedIcon(
                onClick = { navigateTo(plusBtn.route, null) },
                description = "Plus",
                painter = painterResource(plusBtn.icon),
                size = 60.dp,
                isSelected = true,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedContainerColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun SubNavRow(
    navList: List<TypeBottomNavigation>,
    currentRoute: String?,
    navigateTo: (ScreenRoute) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        navList.forEach {
            RoundedIcon(
                onClick = { navigateTo(it.route) },
                description = ScreenRoute.Home.route,
                isSelected = currentRoute == it.route.route,
                size = 30.dp,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                painter = painterResource(it.icon)
            )
        }
    }
}