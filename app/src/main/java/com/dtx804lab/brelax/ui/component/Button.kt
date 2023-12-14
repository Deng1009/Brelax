package com.dtx804lab.brelax.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dtx804lab.brelax.ui.theme.Typography

@Preview
@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infinite = rememberInfiniteTransition(label = "infinity")
    val alpha by infinite.animateFloat(
        label = "alpha",
        initialValue = 1.0f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1.0f at 0 with EaseInOut
                0.75f at 1000
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    val trans by infinite.animateFloat(
        label = "trans",
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0f at 0
                30f at 1000
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(50.dp)
            .height(50.dp)
            .graphicsLayer(alpha = alpha, translationX = trans)
            .clip(shape = CircleShape)
            .background(color = Color.DarkGray)
            .clickable { onClick() }
    ) {
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
fun ClickButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    color: Color = Color.White,
    onClick: () -> Unit = {}
) {
    var isPressed by remember {
        mutableStateOf(false)
    }
    val filterColor by animateColorAsState(
        targetValue =
        if (isPressed) Color.DarkGray.copy(alpha = 0.25f)
        else Color.Transparent,
        animationSpec = tween(100, easing = LinearEasing),
        label = "backgroundColor"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color)
            .pointerInput(onClick) {
                awaitEachGesture {
                    awaitFirstDown().run { isPressed = true }
                    waitForUpOrCancellation()?.run {
                        isPressed = false
                        onClick()
                    } ?: run { isPressed = false }
                }
            }
            .drawBehind {
                drawRect(filterColor)
            }
    ) {
        Text(
            text = text,
            color = Color.DarkGray,
            style = Typography.labelSmall,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}

@Preview
@Composable
fun NegativeButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    onClick: () -> Unit = {}
) {
    ClickButton(
        modifier = modifier
            .border(
                width = 3.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(50.dp)
            ),
        text = text,
        onClick = onClick
    )
}

@Preview
@Composable
fun PreviousButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var isPressed by remember {
        mutableStateOf(false)
    }
    val color by animateColorAsState(
        targetValue =
        if (isPressed) Color.DarkGray.copy(alpha = 0.25f)
        else Color.Transparent,
        animationSpec = tween(100, easing = LinearEasing),
        label = "backgroundColor"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(50.dp)
            .height(50.dp)
            .clip(shape = CircleShape)
            .background(color)
            .pointerInput(onClick) {
                awaitEachGesture {
                    awaitFirstDown().run { isPressed = true }
                    waitForUpOrCancellation()?.run {
                        isPressed = false
                        onClick()
                    } ?: run { isPressed = false }
                }
            }
    ) {
        Icon(
            Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
fun EditButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(20.dp)
            .height(20.dp)
            .pointerInput(onClick) {
                awaitEachGesture {
                    waitForUpOrCancellation()?.run {
                        onClick()
                    }
                }
            }
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = null,
            tint = Color.DarkGray.copy(alpha = 0.75f),
            modifier = Modifier
                .fillMaxSize()
        )
    }
}