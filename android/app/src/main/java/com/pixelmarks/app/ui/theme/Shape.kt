package com.pixelmarks.app.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

/**
 * Procedural Material 3 Expressive Cookie Shape with N lobes
 */
class CookieShape(val lobes: Int = 9, val depth: Float = 0.12f) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = minOf(cx, cy)
        val count = 96
        val tau = (2 * PI).toFloat()

        for (i in 0 until count) {
            val angle = -PI.toFloat() / 2f + (i.toFloat() / count) * tau
            val u = (1f - cos(lobes * angle)) / 2f
            val r = maxRadius * (1f - depth * Math.pow(u.toDouble(), 1.4).toFloat())
            val x = cx + cos(angle) * r
            val y = cy + sin(angle) * r

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return Outline.Generic(path)
    }
}

/**
 * Procedural Material 3 Expressive Sunny / Star Shape
 */
class SunnyShape(val rays: Int = 8, val depth: Float = 0.18f) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = minOf(cx, cy)
        val count = 96
        val tau = (2 * PI).toFloat()

        for (i in 0 until count) {
            val angle = -PI.toFloat() / 2f + (i.toFloat() / count) * tau
            val v = (1f + cos(rays * angle)) / 2f
            val r = maxRadius * (1f - depth + depth * Math.pow(v.toDouble(), 1.8).toFloat())
            val x = cx + cos(angle) * r
            val y = cy + sin(angle) * r

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return Outline.Generic(path)
    }
}

/**
 * Procedural Material 3 Expressive Clover Shape
 */
class CloverShape(val petals: Int = 4, val depth: Float = 0.32f) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = minOf(cx, cy)
        val count = 96
        val tau = (2 * PI).toFloat()

        for (i in 0 until count) {
            val angle = (i.toFloat() / count) * tau
            val r = maxRadius * (1f - depth + depth * Math.pow(Math.abs(cos((petals / 2f) * angle)).toDouble(), 0.6).toFloat())
            val x = cx + cos(angle) * r
            val y = cy + sin(angle) * r

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return Outline.Generic(path)
    }
}

fun getExpressiveShapeByName(name: String): Shape {
    return when (name.lowercase()) {
        "cookie4" -> CookieShape(lobes = 4, depth = 0.22f)
        "cookie6" -> CookieShape(lobes = 6, depth = 0.17f)
        "cookie9" -> CookieShape(lobes = 9, depth = 0.12f)
        "cookie12" -> CookieShape(lobes = 12, depth = 0.09f)
        "sunny", "star" -> SunnyShape(rays = 8, depth = 0.18f)
        "burst" -> SunnyShape(rays = 12, depth = 0.24f)
        "clover4", "flower" -> CloverShape(petals = 4)
        "clover8" -> CloverShape(petals = 8, depth = 0.22f)
        "pill" -> RoundedCornerShape(percent = 50)
        else -> CookieShape(lobes = 9)
    }
}
