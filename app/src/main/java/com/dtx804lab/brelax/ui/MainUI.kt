package com.dtx804lab.brelax.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dtx804lab.brelax.R
import com.dtx804lab.brelax.ui.theme.BrelaxTheme
import com.dtx804lab.brelax.ui.theme.LightBlue
import com.dtx804lab.brelax.ui.theme.LightGreen
import com.dtx804lab.brelax.ui.theme.LightOrange
import com.dtx804lab.brelax.ui.theme.LightPurple
import com.dtx804lab.brelax.ui.theme.LightYellow
import com.dtx804lab.brelax.ui.theme.Typography

@Preview
@Composable
fun MainScreen(
    onGame: () -> Unit = {},
    onDiary: () -> Unit = {},
    onHistory: () -> Unit = {},
    onSurvey: () -> Unit = {}
) {
    BrelaxTheme {
        MainBackground(background = MaterialTheme.colorScheme.background) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 8.dp)
            ) {
                AccountButton(imageID = R.drawable.ic_missing_image)
                Divider(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(3.dp)
                )
                PageButton(
                    imageID = R.drawable.ic_missing_image,
                    text = stringResource(R.string.game_button_name),
                    color = LightBlue,
                    onClick = onGame
                )
                PageButton(
                    imageID = R.drawable.ic_missing_image,
                    text = stringResource(R.string.diary_button_name),
                    color = LightGreen,
                    onClick = onDiary
                )
                PageButton(
                    imageID = R.drawable.ic_missing_image,
                    text = stringResource(R.string.record_button_name),
                    color = LightOrange,
                    onClick = onHistory
                )
                PageButton(
                    imageID = R.drawable.ic_missing_image,
                    text = stringResource(R.string.stai_button_name),
                    color = LightPurple,
                    onClick = onSurvey
                )
            }
        }
    }
}

@Composable
fun MainBackground(background: Color, path: Path = Path(), content: @Composable () -> Unit) = Surface(
    modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            path.reset()
            path.moveTo(0f, it.height * 0.95f)
            path.cubicTo(
                it.width * 0.35f, it.height * 1f,
                it.width * 0.5f, it.height * 0.85f,
                it.width * 1f, it.height * 0.95f
            )
            path.lineTo(it.width.toFloat(), it.height.toFloat())
            path.lineTo(0f, it.height.toFloat())
        }
        .drawBehind {
            drawRect(color = background)
            drawPath(color = LightYellow, path = path)
        },
    color = Color.Transparent
) {
    content()
}

@Composable
fun AccountButton(imageID: Int) {
    Row(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(LightYellow)
            .clickable {
            }
    ) {
        Image(
            painter = painterResource(id = imageID),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .padding(8.dp)
                .drawBehind {
                    drawCircle(color = Color.White)
                }
        )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Account",
                style = Typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun PageButton(
    imageID: Int,
    text: String,
    color: Color = Color.White,
    onClick: (() -> Unit)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .padding(16.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageID),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .padding(8.dp)
                .clip(shape = RoundedCornerShape(5.dp))
        )
        Text(
            text = text,
            style = Typography.labelMedium,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}