package com.example.campuslostandfound.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Corner shapes from the prototype, read through MaterialTheme.shapes:
 *  - small      → buttons, fields, photos, chat bubbles, toggle segments
 *  - extraLarge → the discard dialog card
 */
val AppShapes = Shapes(
    small = RoundedCornerShape(Dimens.CornerRadius),
    extraLarge = RoundedCornerShape(20.dp),
)
