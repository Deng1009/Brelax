package com.dtx804lab.brelax.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dtx804lab.brelax.ui.theme.BrelaxTheme
import com.dtx804lab.brelax.ui.theme.Typography

@SuppressLint("MissingPermission")
@Preview(name = "scan_device", group = "bluetoothUI")
@Composable
fun ScanDeviceScreen(
    deviceList: SnapshotStateList<BluetoothDevice> = mutableStateListOf(),
    isScanning: MutableState<Boolean> = mutableStateOf(false),
    onScan: (Boolean) -> Unit = {},
    onConnect: (BluetoothDevice) -> Unit = {}
) {
    BrelaxTheme {
        Surface(color = Color.White) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val (title, list, button) = createRefs()
                Text(
                    text = "Device",
                    style = Typography.labelLarge,
                    modifier = Modifier
                        .constrainAs(title) {
                            top.linkTo(parent.top, 8.dp)
                            start.linkTo(parent.start, 16.dp)
                            width = Dimension.wrapContent
                            height = Dimension.wrapContent
                        }
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .constrainAs(list) {
                            top.linkTo(title.bottom, 8.dp)
                            bottom.linkTo(button.top, 8.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end, 16.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                ) {
                    items(deviceList) { device ->
                        if (device.name == null) return@items
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 3.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable { onConnect(device) }
                        ) {
                            Text(
                                text = device.name,
                                style = Typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = device.address,
                                style = Typography.bodySmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        isScanning.value = !isScanning.value
                        onScan(isScanning.value)
                    },
                    modifier = Modifier
                        .constrainAs(button) {
                            bottom.linkTo(parent.bottom, 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.wrapContent
                            height = Dimension.wrapContent
                        }
                ) {
                    Text(
                        text = if (isScanning.value) "Scanning..." else "Scan",
                        style = Typography.labelMedium
                    )
                }
            }
        }
    }
}