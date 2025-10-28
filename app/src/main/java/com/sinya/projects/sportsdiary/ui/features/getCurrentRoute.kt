package com.sinya.projects.sportsdiary.ui.features

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun getCurrentRoute(navController: NavController): String? {
    val currentBackStack = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStack?.destination?.route
        ?.substringAfterLast('.')
        ?.substringBefore("/")
        ?.substringBefore("?")
    return currentRoute
}
