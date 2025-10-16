package com.sinya.projects.sportsdiary.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp),
    large = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
    extraLarge = CircleShape
)