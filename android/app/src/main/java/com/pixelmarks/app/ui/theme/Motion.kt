package com.pixelmarks.app.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

object ExpressiveMotion {
    // Material 3 Expressive physical spring specs
    val fastSpatial = spring<Float>(
        stiffness = 800f,
        dampingRatio = 0.6f
    )

    val defaultSpatial = spring<Float>(
        stiffness = 380f,
        dampingRatio = 0.8f
    )

    val slowSpatial = spring<Float>(
        stiffness = 200f,
        dampingRatio = 0.8f
    )

    val bouncy = spring<Float>(
        stiffness = 500f,
        dampingRatio = 0.45f
    )

    val fastEffects = spring<Float>(
        stiffness = 3800f,
        dampingRatio = 1.0f
    )
}
