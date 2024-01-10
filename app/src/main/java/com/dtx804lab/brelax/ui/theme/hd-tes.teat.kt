package com.dtx804lab.brelax.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dtx804lab.brelax.ui.Button
import com.dtx804lab.brelax.ui.Button2
import com.dtx804lab.brelax.ui.component.PreviousButton

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
            PreviousButton(
                modifier = Modifier.padding(start = 15.dp, top = 50.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 475.dp)
            ) {
                val list =
                    listOf("iTBS", "cTBS", "TBS", "tDCS", "tACS", "otDcs")
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
                    .padding(top = 25.dp, bottom = 25.dp, start = 100.dp, end = 100.dp)
                    .size(width = 175.dp, height = 35.dp),
                color = Color(0xFFF5EDE1),
                text = "停止"
            )
        }
    }
}