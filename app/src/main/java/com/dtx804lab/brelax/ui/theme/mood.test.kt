package com.dtx804lab.brelax.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dtx804lab.brelax.ui.Button
import com.dtx804lab.brelax.ui.Button
import com.dtx804lab.brelax.ui.Button2

@Preview
@Composable
private fun Screen() {
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
            Button(
                modifier = Modifier
                    .padding(25.dp)
                    .size(width = 50.dp, height = 50.dp),
                color = Color(0xFFF5EDE1),
                text = "<"
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "那天心情如何?",
                style = Typography.labelLarge
            )
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
                        color = Color(0xFFF5EDE1),
                        text = "+ 其他添加"
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFF5EDE1),
                        text = "難過"
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val list =
                    listOf("開心", "幸福", "平靜", "樂觀", "活潑", "無聊", "失望", "生氣", "焦慮")
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
            Button(
                modifier = Modifier
                    .padding(top = 200.dp, bottom = 25.dp,start = 100.dp, end = 100.dp)
                    .size(width = 175.dp, height = 30.dp),
                color = Color(0xFFF5EDE1),
                text = "下一步"
            )
        }
    }
}