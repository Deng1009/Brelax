package com.dtx804lab.brelax.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dtx804lab.brelax.R
import com.dtx804lab.brelax.data.Event
import com.dtx804lab.brelax.data.Feel
import com.dtx804lab.brelax.data.ImageData
import com.dtx804lab.brelax.data.Mood
import com.dtx804lab.brelax.ui.component.ClickButton
import com.dtx804lab.brelax.ui.component.EditButton
import com.dtx804lab.brelax.ui.component.NextButton
import com.dtx804lab.brelax.ui.component.PreviousButton
import com.dtx804lab.brelax.ui.shape.WaveBox
import com.dtx804lab.brelax.ui.theme.BrelaxTheme
import com.dtx804lab.brelax.ui.theme.Typography
import java.util.Calendar
import java.util.Date

@Preview(name = "mood_selection", group = "diaryUI")
@Composable
fun MoodSelectionScreen(
    moodID: MutableState<Float> = remember { mutableFloatStateOf(0f) },
    onBack: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    val mood = Mood.getMood(moodID.value)
    val moodColor by animateColorAsState(
        targetValue = mood.color,
        animationSpec = tween(500, easing = LinearEasing),
        label = "moodColor"
    )
    BrelaxTheme {
        MoodBackground(
            background = Color.White,
            color = moodColor
        ) {
            ConstraintLayout(
              modifier = Modifier
                  .fillMaxSize()
            ) {
                val (previousButton, moodQuest, moodImage,
                    moodText, moodBar, nextButton) = createRefs()
                val textGuideline = createGuidelineFromTop(0.2f)
                val imageStartGuideline = createGuidelineFromStart(0.1f)
                val imageEndGuideline = createGuidelineFromEnd(0.1f)
                PreviousButton(
                    onClick = onBack,
                    modifier = Modifier
                        .constrainAs(previousButton) {
                            top.linkTo(parent.top, 24.dp)
                            start.linkTo(parent.start)
                        }
                )
                Text(
                    text = stringResource(id = R.string.what_mood),
                    style = Typography.labelLarge,
                    modifier = Modifier
                        .constrainAs(moodQuest) {
                            top.linkTo(textGuideline)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Image(
                    painter = painterResource(id = mood.imageID),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .constrainAs(moodImage) {
                            top.linkTo(moodQuest.bottom, 8.dp)
                            start.linkTo(imageStartGuideline)
                            end.linkTo(imageEndGuideline)
                            width = Dimension.fillToConstraints
                        }
                )
                Text(
                    stringResource(id = mood.textID),
                    style = Typography.labelLarge,
                    modifier = Modifier
                        .constrainAs(moodText) {
                            top.linkTo(moodImage.bottom, 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                MoodBar(
                    progress = moodID,
                    modifier = Modifier
                        .constrainAs(moodBar) {
                            top.linkTo(moodText.bottom, 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                NextButton(
                    onClick = onNext,
                    modifier = Modifier
                        .constrainAs(nextButton) {
                            top.linkTo(moodBar.bottom, 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            }
        }
    }
}

/*
* Event:
// val feel = stringResource(id = Mood.getMood(mood).textID)
// description = stringResource(id = R.string.what_event, feel)
// items = Event.values().map { it to mutableStateOf(false) }
* Feel:
// description = stringResource(id = R.string.what_feel)
// items = Feel.values().map { it to mutableStateOf(false) }
*/
@Preview(name = "item_selection", group = "diaryUI")
@Composable
fun ItemSelectionScreen(
    moodID: Float = 0f,
    description: String = stringResource(id = R.string.what_feel),
    items: SnapshotStateMap<out ImageData, MutableState<Boolean>> =
        mutableStateMapOf(*(Feel.values().map { it to mutableStateOf(false) }.toTypedArray())),
    onBack: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    BrelaxTheme {
        MoodBackground(
            background = Color.White,
            color = Mood.getMood(moodID).color
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                val (previousButton, text, selectionGrid, nextButton) = createRefs()
                val textGuideline = createGuidelineFromTop(0.2f)
                val nextGuideline = createGuidelineFromBottom(50.dp + 16.dp)
                PreviousButton(
                    onClick = onBack,
                    modifier = Modifier
                        .constrainAs(previousButton) {
                            top.linkTo(parent.top, 24.dp)
                            start.linkTo(parent.start)
                        }
                )
                Text(
                    text = description,
                    textAlign = TextAlign.Center,
                    style = Typography.labelLarge,
                    modifier = Modifier
                        .constrainAs(text) {
                            top.linkTo(textGuideline)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .constrainAs(selectionGrid) {
                            top.linkTo(text.bottom, 16.dp)
                            bottom.linkTo(nextGuideline, 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                ) {
                    items.forEach { (event, state) ->
                        item {
                            SelectionButton(
                                selected = remember { state },
                                buttonDescription = stringResource(id = event.textID),
                                painter = painterResource(id = event.imageID)
                            )
                        }
                    }
                }
                NextButton(
                    onClick = onNext,
                    modifier = Modifier
                        .constrainAs(nextButton) {
                            top.linkTo(nextGuideline)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            }
        }
    }
}

@Preview(name = "diary", group = "diaryUI")
@Composable
fun DiaryScreen(
    moodID: Float = 0f,
    date: Date = Date(),
    events: List<Event> = listOf(Event.TRAVEL, Event.FOOD),
    feels: List<Feel> = listOf(Feel.LUCKY),
    diaryTitle: MutableState<String> = mutableStateOf(""),
    diaryContent: MutableState<String> = mutableStateOf(""),
    onEventClick: () -> Unit = {},
    onFeelClick: () -> Unit = {},
    onBack: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    val mood = Mood.getMood(moodID)
    val time = Calendar.getInstance().apply {
        time = date
    }
    val timeState =
        if (time.get(Calendar.HOUR_OF_DAY) < 12) stringResource(id = R.string.am)
        else stringResource(id = R.string.pm)
    BrelaxTheme {
        MoodBackground(
            background = Color.White,
            color = mood.color,
        ) {
            WaveBox(ratio = 0.3f) {
                drawRect(Color.White.copy(alpha = 0.5f))
            }
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val (previousButton, dateText, moodText, moodImage,
                    eventList, feelList, diary, completedButton) = createRefs()
                val moodItemGuideline = createGuidelineFromTop(0.2f)
                val diaryItemGuideline = createGuidelineFromTop(0.325f)
                PreviousButton(
                    onClick = onBack,
                    modifier = Modifier
                        .constrainAs(previousButton) {
                            top.linkTo(parent.top, 24.dp)
                            start.linkTo(parent.start)
                        }
                )
                Text(
                    text = stringResource(
                        id = R.string.diary_date,
                        time.get(Calendar.MONTH) + 1,
                        time.get(Calendar.DAY_OF_MONTH),
                        timeState,
                        time.get(Calendar.HOUR),
                        time.get(Calendar.MINUTE)
                    ),
                    style = Typography.labelLarge,
                    modifier = Modifier
                        .constrainAs(dateText) {
                            top.linkTo(parent.top, 24.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                Text(
                    text = stringResource(id = mood.textID),
                    style = Typography.labelLarge,
                    modifier = Modifier
                        .constrainAs(moodText) {
                            top.linkTo(moodItemGuideline)
                            start.linkTo(parent.start, 24.dp)
                            width = Dimension.wrapContent
                        }
                )
                Image(
                    painter = painterResource(id = mood.imageID),
                    contentDescription = stringResource(id = mood.textID),
                    modifier = Modifier
                        .constrainAs(moodImage) {
                            top.linkTo(moodItemGuideline)
                            bottom.linkTo(diaryItemGuideline)
                            start.linkTo(moodText.end)
                            end.linkTo(parent.end)
                            height = Dimension.fillToConstraints
                        }
                        .aspectRatio(1f)
                )
                SelectedList(
                    title = stringResource(id = R.string.event_list_title),
                    items = events,
                    onClick = onEventClick,
                    modifier = Modifier
                        .constrainAs(eventList) {
                            top.linkTo(diaryItemGuideline)
                            start.linkTo(parent.start, 24.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                )
                SelectedList(
                    title = stringResource(id = R.string.feel_list_title),
                    items = feels,
                    onClick = onFeelClick,
                    modifier = Modifier
                        .constrainAs(feelList) {
                            top.linkTo(eventList.bottom)
                            start.linkTo(parent.start, 24.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                )
                DiaryArea(
                    title = remember { diaryTitle },
                    content = remember { diaryContent },
                    modifier = Modifier
                        .constrainAs(diary) {
                            top.linkTo(feelList.bottom)
                            bottom.linkTo(completedButton.top, 8.dp)
                            start.linkTo(parent.start, 24.dp)
                            end.linkTo(parent.end, 24.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                )
                ClickButton(
                    text = stringResource(id = R.string.completed_text),
                    onClick = onComplete,
                    modifier = Modifier
                        .constrainAs(completedButton) {
                            bottom.linkTo(parent.bottom, 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            }
        }
    }
}

@Composable
fun MoodBackground(
    background: Color,
    color: Color,
    content: @Composable () -> Unit
) = Surface(
    color = background
) {
    WaveBox(ratio = 0.15f) {
        drawRect(
            Brush.verticalGradient(
                listOf(color.copy(alpha = 0.3f), color)
            )
        )
    }
    content()
}

@Composable
fun MoodBar(
    progress: MutableState<Float>,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit = {}
) {
    Slider(
        value = progress.value,
        onValueChange = {
            progress.value = it
        },
        valueRange = 0f..4f,
        steps = 3,
        colors = SliderDefaults.colors(
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent,
            inactiveTrackColor = Color.LightGray,
            activeTrackColor = Color.LightGray,
            thumbColor = Color.DarkGray
        ),
        modifier = modifier
            .padding(start = 32.dp, end = 32.dp)
    )
}

@Composable
fun SelectionButton(
    selected: MutableState<Boolean>,
    buttonDescription: String,
    painter: Painter
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val selectedColor = animateColorAsState(
            targetValue =
            if (selected.value) Color.Black.copy(alpha = 0.25f)
            else Color.Transparent,
            animationSpec = tween(100, easing = LinearEasing),
            label = "moodColor"
        )
        Image(
            painter = painter,
            contentDescription = buttonDescription,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .indication(
                    remember {
                        MutableInteractionSource()
                    },
                    indication = null
                )
                .selectable(
                    selected = selected.value,
                    role = Role.RadioButton
                ) {
                    selected.value = !selected.value
                }
                .shadow(
                    elevation = 10.dp,
                    spotColor = Color.DarkGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .drawWithContent {
                    drawRect(Color.White)
                    drawContent()
                    drawRect(
                        Brush.radialGradient(
                            listOf(Color.Transparent, selectedColor.value)
                        )
                    )
                }
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = buttonDescription,
            style = Typography.labelSmall
        )
    }
}

@Composable
fun SelectedList(
    title: String,
    items: List<ImageData>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = Typography.labelMedium
            )
            Spacer(modifier = Modifier.size(8.dp))
            EditButton(onClick = onClick)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
        ) {
            items(items) { item ->
                IconItem(
                    description = stringResource(id = item.textID),
                    icon = painterResource(id = item.imageID)
                )
            }
        }
    }
}

@Composable
fun IconItem(description: String, icon: Painter) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .aspectRatio(2f)
            .drawBehind {
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(20f, 20f)
                )
            }
    ) {
        Image(
            painter = icon,
            contentDescription = description,
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(1f)
        )
        Text(
            text = description,
            style = Typography.bodySmall,
            modifier = Modifier
                .padding(end = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryArea(
    title: MutableState<String>,
    content: MutableState<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (text, titleText, contentText) = createRefs()
            Text(
                text = stringResource(id = R.string.diary_title),
                style = Typography.labelMedium,
                modifier = Modifier
                    .constrainAs(text) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
            )
            OutlinedTextField(
                value = title.value,
                onValueChange = {
                    title.value = it
                },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.title_text),
                        style = Typography.labelSmall
                    )
                },
                textStyle = Typography.bodySmall,
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedBorderColor = Color.DarkGray
                ),
                modifier = Modifier
                    .constrainAs(titleText) {
                        top.linkTo(text.bottom, 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
            )
            OutlinedTextField(
                value = content.value,
                onValueChange = {
                    content.value = it
                },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.content_text),
                        style = Typography.labelSmall
                    )
                },
                textStyle = Typography.bodySmall,
                maxLines = Int.MAX_VALUE,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedBorderColor = Color.DarkGray
                ),
                modifier = Modifier
                    .constrainAs(contentText) {
                        top.linkTo(titleText.bottom, 16.dp)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
        }
    }
}
