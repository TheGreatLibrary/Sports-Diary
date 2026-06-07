package com.sinya.projects.sportsdiary.core.domain.model

sealed interface TypeTopNavigation {
    val onBackClick: () -> Unit

    data class FullTopBar(
        override val onBackClick: () -> Unit,
        val title: String,
        val onSettingsClick: () -> Unit
    ) : TypeTopNavigation

    data class BackIconWithTitle(
        override val onBackClick: () -> Unit,
        val title: String
    ) : TypeTopNavigation

    data class OnlyBackIcon(
        override val onBackClick: () -> Unit
    ) : TypeTopNavigation
}