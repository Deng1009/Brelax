package com.dtx804lab.brelax.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dtx804lab.brelax.R
import com.dtx804lab.brelax.ui.theme.Typography

@Preview
@Composable
fun Screen() {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(
                    rememberScrollState(),
                    orientation = Orientation.Vertical
                )
        ) {
            Text(
                modifier = Modifier.padding(top = 100.dp, start = 8.dp),
                text = "歡迎回來",
                style = Typography.labelLarge
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "你的旅程中只是開始，",
                    style = Typography.bodySmall
                )
                Text(
                    text = "還有很多禮物和驚喜等著你!",
                    style = Typography.bodySmall
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlanButton()

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text("管理", style = Typography.labelMedium)
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFE3E8),
                        text = "心情日記"
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFBBDEFF),
                        text = "快速放鬆"
                    )
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
            ) {
                Text("穿戴裝置", style = Typography.labelMedium)


            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(8.dp)
                )
                {

                    Button(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFD083),
                        painter = painterResource(id = R.drawable.candle),
                        text = "HD-tES"
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFECAB),
                        text = "fNIRS"
                    )
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
            ) {
                Text("問卷測驗", style = Typography.labelMedium)


            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val list = listOf("STAI", "GAD-7", "BAI", "BDI", "HAMA-A")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    items(list) {
                        Button2(it)
                        //LazyVerticalGrid列表網格
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PlanButton(
    color: Color = Color.White,
    modifier: Modifier = Modifier,
    text: String = "Text"
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFFFD692))
            .width(375.dp)
            .aspectRatio(3f),
    ) {
    }

}

@Preview
@Composable
fun Button(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    painter: Painter = painterResource(id = R.drawable.ic_missing_image),
    text: String = "Textasfasffasf"
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(color, color.copy(alpha = 0.5f))
                )
            )
            .width(200.dp)
            .aspectRatio(1.75f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(10.dp)
                .aspectRatio(1f),
            painter = painter,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = text,
            style = Typography.bodySmall
        )
    }
}


@Preview
@Composable
fun Button2(
    text: String = "Text"
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF5EDE1))
            .aspectRatio(3f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}