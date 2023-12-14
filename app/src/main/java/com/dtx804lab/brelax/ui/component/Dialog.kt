package com.dtx804lab.brelax.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dtx804lab.brelax.R
import com.dtx804lab.brelax.ui.theme.Typography

@Preview
@Composable
fun WaitingDialog(hint: String = "Text") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = hint,
            style = Typography.bodySmall
        )
    }
}

@Preview
@Composable
fun CheckDialog(
    text: String = "Text",
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            style = Typography.bodyMedium
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row {
            NegativeButton(
                text = stringResource(id = R.string.cancel),
                onClick = onCancel
            )
            Spacer(modifier = Modifier.size(16.dp))
            ClickButton(
                text = stringResource(id = R.string.confirm),
                onClick = onConfirm
            )
        }
    }
}