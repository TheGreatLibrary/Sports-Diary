package com.sinya.projects.domain.model

import androidx.annotation.DrawableRes

data class RadioItem<T>(
    val text: String?,
    @DrawableRes val icon: Int? = null,
    val value: T? = null
)