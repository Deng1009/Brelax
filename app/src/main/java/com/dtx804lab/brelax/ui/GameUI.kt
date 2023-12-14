package com.dtx804lab.brelax.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dtx804lab.brelax.ui.theme.BrelaxTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.compose.legend.verticalLegend
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Preview
@Composable
fun GameScreen(
    chestData: SnapshotStateList<Float> = remember { mutableStateListOf(0f, 1f, 2f, 3f) },
    chestBaseline: MutableFloatState = mutableFloatStateOf(0f),
    bellyData: SnapshotStateList<Float> = remember { mutableStateListOf(3f, 2f, 1f, 0f) },
    bellyBaseline: MutableFloatState = remember { mutableFloatStateOf(0f) },
    breathFactor: MutableFloatState = remember { mutableFloatStateOf(0f) }
) {
    val scope = rememberCoroutineScope()
    val backgroundColor = Color.White
    val sizeFactor = remember { Animatable(0f) }

    BrelaxTheme {
        Surface(
            color = backgroundColor
        ) {
            LaunchedEffect(Unit) {
                scope.launch {
                    while (isActive) {
                        sizeFactor.animateTo(
                            targetValue = breathFactor.floatValue * 0.3f,
                            animationSpec = tween(50, easing = LinearEasing)
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                DataChart(
                    label = "Chest Breathing",
                    color = Color.Cyan,
                    data = chestData,
                    baseline = chestBaseline.floatValue
                )
                DataChart(
                    label = "Belly Breathing",
                    color = Color.Blue,
                    data = bellyData,
                    baseline = bellyBaseline.floatValue
                )
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                val size = 150.dp * (1 + sizeFactor.value)
                Box(modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .width(size)
                    .height(size)
                )
            }

        }
    }
}

@Composable
private fun DataChart(
    label: String,
    color: Color,
    data: List<Float>,
    baseline: Float
) {
    val entry = run {
        data.map { it - baseline }
            .toTypedArray()
            .let { entryModelOf(*it) }
    }
    Text(text = "%.2f".format(data.last()))
    Chart(
        modifier = Modifier.padding(8.dp),
        chart = lineChart(
            lines = listOf(colorLineSpec(color))
        ),
        model = entry,
        startAxis = rememberStartAxis(
            guideline = null,
        ),
        bottomAxis = rememberBottomAxis(
            guideline = null,
            itemPlacer = AxisItemPlacer.Horizontal.default(
                spacing = if (data.size < 5) 1 else data.size / 5
            ),
        ),
        chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
        legend = dataLegend(label = label, color = color)
    )
}

@Composable
fun colorLineSpec(color: Color): LineChart.LineSpec {
    return LineChart.LineSpec(
        lineColor = color.toArgb(),
        lineBackgroundShader = DynamicShaders.fromBrush(
            Brush.verticalGradient(
                listOf(
                    color.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                    color.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END),
                ),
            ),
        ),
    )
}

@Composable
private fun dataLegend(label: String, color: Color) = verticalLegend(
    items = listOf(
        legendItem(
            icon = shapeComponent(Shapes.pillShape, color),
            label = textComponent(
                color = currentChartStyle.axis.axisLabelColor,
                textSize = 12.sp,
                typeface = android.graphics.Typeface.MONOSPACE,
            ),
            labelText = label
        )
    ),
    iconSize = 8.dp,
    iconPadding = 8.dp,
    spacing = 4.dp,
    padding = dimensionsOf(top = 8.dp),
)