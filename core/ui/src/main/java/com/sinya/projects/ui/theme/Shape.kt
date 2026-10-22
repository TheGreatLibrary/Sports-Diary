package com.sinya.projects.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(20.dp),
    medium = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp),
    large = RoundedCornerShape(30.dp),
    extraLarge = CircleShape
)