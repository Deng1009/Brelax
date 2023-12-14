package com.dtx804lab.brelax.ui.shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class FireShape(private val fireTopH: Float, private val fireTopV: Float): Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path()
                .apply {
                    val leftBound = size.width * 0.4f
                    val rightBound = size.width * 0.6f
                    val top = Offset(
                        size.width * (0.5f + fireTopH),
                        size.height * (0.55f + fireTopV)
                    )
                    reset()
                    moveTo(top.x, top.y)
                    cubicTo(
                        size.width * 0.45f, size.height * 0.6f,
                        size.width * 0.6f, size.height * 0.7f,
                        rightBound, size.height * 0.75f
                    )
                    cubicTo(
                        rightBound, size.height * 0.82f,
                        leftBound, size.height * 0.82f,
                        leftBound, size.height * 0.75f
                    )
                    cubicTo(
                        size.width * 0.45f, size.height * 0.7f,
                        size.width * 0.4f, size.height * 0.6f,
                        top.x, top.y
                    )
                }
        )
    }

}