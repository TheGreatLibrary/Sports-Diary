package com.sinya.projects.sportsdiary.domain.enums

import androidx.annotation.DrawableRes

sealed interface TypeAppTopNavigation {
    val onBackClick: () -> Unit

    data class WithoutIcon(
        override val onBackClick: () -> Unit,
        val title: String
    ) : TypeAppTopNavigation

    data class WithIcon(
        override val onBackClick: () -> Unit,
        val title: String,
        @DrawableRes val painter: Int,
        val onClick: () -> Unit
    ) : TypeAppTopNavigation

    data class OnlyBackIcon(
        override val onBackClick: () -> Unit
    ) : TypeAppTopNavigation
}