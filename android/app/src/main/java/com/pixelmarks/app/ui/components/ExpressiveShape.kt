package com.pixelmarks.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixelmarks.app.ui.theme.ExpressiveMotion
import com.pixelmarks.app.ui.theme.getExpressiveShapeByName

@Composable
fun MorphShape(
    shapeName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    rotation: Float = 0f,
    content: @Composable (() -> Unit)? = null
) {
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = ExpressiveMotion.defaultSpatial,
        label = "shape_rotation"
    )

    val shape = getExpressiveShapeByName(shapeName)

    Box(
        modifier = modifier
            .size(size)
            .rotate(animatedRotation)
            .clip(shape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            content()
        }
    }
}
