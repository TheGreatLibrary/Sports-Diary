package com.sinya.projects.sportsdiary.navigation

import androidx.annotation.DrawableRes

sealed interface TypeTopNavigation {
    val onBackClick: () -> Unit

    data class WithoutIcon(
        override val onBackClick: () -> Unit,
        val title: String
    ) : TypeTopNavigation

    data class WithIcon(
        override val onBackClick: () -> Unit,
        val title: String,
        @DrawableRes val painter: Int,
        val onClick: () -> Unit
    ) : TypeTopNavigation

    data class OnlyBackIcon(
        override val onBackClick: () -> Unit
    ) : TypeTopNavigation
}