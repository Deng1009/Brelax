package com.dtx804lab.brelax.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dtx804lab.brelax.R
import com.dtx804lab.brelax.ui.theme.Typography
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequiresPermissionsDialog(
    onDismiss: () -> Unit
) {

    val permissionList = mutableListOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissionList.add(android.Manifest.permission.BLUETOOTH_SCAN)
        permissionList.add(android.Manifest.permission.BLUETOOTH_CONNECT)
    }

    val permissionsState = rememberMultiplePermissionsState(permissionList)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray)
            .padding(bottom = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White),
    ) {
        Text(
            text = stringResource(
                id = R.string.permission_description,
                permissionList.joinToString(separator = "\n") {
                    "\t•${permissionTranslate(it)}"
                }
            ),
            style = Typography.bodyMedium.copy(

            ),
            modifier = Modifier
                .padding(8.dp)
        )
        Divider(color = Color.LightGray)
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    onDismiss()
                    permissionsState.launchMultiplePermissionRequest()
                }
        ) {
            Text(
                text = stringResource(id = R.string.permission_continue),
                style = Typography.labelMedium
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
    }

}

private fun permissionTranslate(permission: String): String {
    return when (permission) {
        android.Manifest.permission.ACCESS_FINE_LOCATION -> "位置"
        android.Manifest.permission.BLUETOOTH_SCAN -> "藍牙掃描"
        android.Manifest.permission.BLUETOOTH_CONNECT -> "藍芽連接"
        else -> ""
    }
}