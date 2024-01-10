package com.dtx804lab.brelax.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dtx804lab.brelax.ui.component.ClickButton
import com.dtx804lab.brelax.ui.component.PreviousButton
import com.google.firebase.inappmessaging.model.Button

@Preview
@Composable
private fun Screen() =
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
            PreviousButton(
                modifier = Modifier.padding(start = 15.dp, top = 50.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 275.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "歡迎來到呼吸訓練"
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp),
                text = "首先選擇呼吸方式"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 425.dp)
                .padding(horizontal = 16.dp),
            // 添加水平边距，使按钮与屏幕边缘保持一致,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            Button(
                text = "4-7-8 呼吸法"
            )
            Button(
                text = "四方 呼吸法"
            )
            Button(
                text = "橫膈膜深呼吸法"
            )
        }
    }

@Composable
private fun Button(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.White
) {
    Box(
        modifier
            .height(30.dp)
            .padding(30.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color)
    ) {
        Text(
            text = text,
            style = Typography.labelMedium,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
    }
}