package com.sinya.projects.domain.model

import android.graphics.Color

data class HealthProgressData(
    val title: String,
    val icon: Int,
    val color: Color,
    val progress: Float,
    val count: String
)