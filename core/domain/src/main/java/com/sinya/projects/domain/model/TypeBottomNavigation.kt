package com.sinya.projects.domain.model

import androidx.annotation.DrawableRes

data class TypeBottomNavigation(
    val key: String,
    @DrawableRes val icon: Int,
    val onClick: () -> Unit
)