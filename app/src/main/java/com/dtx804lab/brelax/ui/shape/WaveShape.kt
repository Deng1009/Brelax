package com.dtx804lab.brelax.ui.shape

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class WaveShape(private val ratio: Float): Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path()
                .apply {
                    reset()
                    moveTo(0f, size.height * ratio)
                    cubicTo(
                        size.width * 0.35f, size.height * (ratio + 0.05f),
                        size.width * 0.5f, size.height * (ratio - 0.1f),
                        size.width * 1f, size.height * ratio
                    )
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                }
        )
    }

}

@Composable
fun WaveBox(
    ratio: Float,
    modifier: Modifier = Modifier,
    draw: DrawScope.() -> Unit = {}
) = Box(
    modifier = modifier
        .fillMaxSize()
        .clip(shape = WaveShape(ratio))
        .drawBehind {
            draw()
        }
) {}