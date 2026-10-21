package com.pixelmarks.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixelmarks.app.ui.theme.getExpressiveShapeByName

private val LOADING_SHAPES = listOf(
    "burst",
    "cookie9",
    "clover4",
    "sunny",
    "cookie4",
    "pill"
)

@Composable
fun MorphingLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "m3e_loader")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loader_rotation"
    )

    val step = ((rotation / 60f).toInt()) % LOADING_SHAPES.size
    val currentShape = LOADING_SHAPES[step]

    Box(
        modifier = modifier
            .size(size)
            .rotate(rotation)
            .clip(getExpressiveShapeByName(currentShape))
            .background(color)
    )
}
