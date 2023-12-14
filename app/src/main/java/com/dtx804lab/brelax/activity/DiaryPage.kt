package com.dtx804lab.brelax.activity

import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dtx804lab.brelax.R
import com.dtx804lab.brelax.data.Event
import com.dtx804lab.brelax.data.Feel
import com.dtx804lab.brelax.data.ImageData
import com.dtx804lab.brelax.data.Mood
import com.dtx804lab.brelax.database.Diary
import com.dtx804lab.brelax.database.DiaryDao
import com.dtx804lab.brelax.ui.DiaryScreen
import com.dtx804lab.brelax.ui.ItemSelectionScreen
import com.dtx804lab.brelax.ui.MoodSelectionScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.Date

class DiaryPage : PageDestination("Diary"), KoinComponent {

    companion object {
        private const val MOOD = "mood"
        private const val EVENTS = "events"
        private const val FEELS = "feels"

        private val MOOD_SELECTION = PageDestination("Diary_MoodSelection")
        private val EVENT_SELECTION = PageDestination(
            "Diary_EventSelection",
            mapOf(
                MOOD to NavType.FloatType,
                EVENTS to NavType.StringArrayType,
                FEELS to NavType.StringArrayType
            )
        )
        private val FEEL_SELECTION = PageDestination(
            "Diary_FeelSelection",
            mapOf(
                MOOD to NavType.FloatType,
                EVENTS to NavType.StringArrayType,
                FEELS to NavType.StringArrayType
            )
        )
        private val EDIT_DIARY = PageDestination(
            "Diary_EditDiary",
            mapOf(
                MOOD to NavType.FloatType,
                EVENTS to NavType.StringArrayType,
                FEELS to NavType.StringArrayType
            )
        )
    }

    private fun <T : ImageData> getItemList(items: Map<T, MutableState<Boolean>>): List<T> {
        return items.filter { it.value.value }.map { it.key }
    }

    private fun isItemNotSelect(items: Map<out ImageData, MutableState<Boolean>>): Boolean {
        return items.filter { it.value.value }.isEmpty()
    }

    private fun <T : ImageData> setSelectItem(
        items: Map<T, MutableState<Boolean>>,
        list: List<T>
    ) {
        list.forEach {
            items[it]?.value = true
        }
    }

    private fun navigateToEventSelection(
        navController: NavController,
        mood: Float,
        events: List<Event> = listOf(),
        feels: List<Feel> = listOf()
    ) {
        navController.goto(
            EVENT_SELECTION, mood,
            events.map { it.name },
            feels.map { it.name }
        )
    }

    private fun navigateToFeelSelection(
        navController: NavController,
        mood: Float,
        events: List<Event>,
        feels: List<Feel> = listOf()
    ) {
        navController.goto(
            FEEL_SELECTION, mood,
            events.map { it.name },
            feels.map { it.name }
        )
    }

    private fun navigateToDiary(
        navController: NavController,
        mood: Float,
        events: List<Event>,
        feels: List<Feel>
    ) {
        navController.goto(
            EDIT_DIARY, mood,
            events.map { it.name },
            feels.map { it.name }
        ) {
            popUpTo(MainActivity.MAIN.route)
            launchSingleTop = true
        }
    }

    override fun NavGraphBuilder.addGraph(
        controller: NavController,
        onPrevious: () -> Unit
    ) {
        println(EVENT_SELECTION.route)
        navigation(
            route = this@DiaryPage.route,
            startDestination = MOOD_SELECTION.route
        ) {
            composable(
                MOOD_SELECTION.route,
            ) {stackEntry ->
                val mood = remember {
                    mutableFloatStateOf(stackEntry.savedStateHandle["mood"]?: 0f)
                }
                MoodSelectionScreen(
                    moodID = mood,
                    onBack = { onPrevious() },
                    onNext = {
                        stackEntry.savedStateHandle["mood"] = mood.floatValue
                        navigateToEventSelection(controller, mood.floatValue)
                    }
                )
            }
            composable(
                route = EVENT_SELECTION.route,
                arguments = EVENT_SELECTION.args
            ) { stackEntry ->
                val mood = stackEntry.arguments!!.getFloat(MOOD)
                val feel = stringResource(id = Mood.getMood(mood).textID)
                val events = stackEntry.arguments!!.getStringList(EVENTS)
                    ?.map {Event.valueOf(it) } ?: listOf()
                val selectedEvent = remember {
                    val map = Event.values().map { it to mutableStateOf(false) }
                    mutableStateMapOf(*(map.toTypedArray()))
                }
                if (events.isNotEmpty()) setSelectItem(selectedEvent, events)
                if (stackEntry.savedStateHandle.contains("event")) {
                    setSelectItem(selectedEvent, stackEntry.savedStateHandle["event"]!!)
                }
                ItemSelectionScreen(
                    moodID = mood,
                    description = stringResource(id = R.string.what_event, feel),
                    items = selectedEvent,
                    onBack = { controller.popBackStack() },
                    onNext = {
                        if (isItemNotSelect(selectedEvent)) {
                            Toast.makeText(
                                controller.context,
                                R.string.not_select_item_warning,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            stackEntry.savedStateHandle["event"] = getItemList(selectedEvent)
                            if (events.isEmpty()) {
                                navigateToFeelSelection(
                                    controller, mood,
                                    stackEntry.savedStateHandle["event"]!!
                                )
                            } else {
                                val feels = stackEntry.arguments!!.getStringList(FEELS)
                                    ?.map { Feel.valueOf(it) } ?: listOf()
                                navigateToDiary(
                                    controller, mood,
                                    stackEntry.savedStateHandle["event"]!!, feels
                                )
                            }
                        }
                    }
                )
            }
            composable(
                route = FEEL_SELECTION.route,
                arguments = FEEL_SELECTION.args
            ) { stackEntry ->
                val mood = stackEntry.arguments!!.getFloat(MOOD)
                val feels = stackEntry.arguments!!.getStringList(FEELS)
                    ?.map { Feel.valueOf(it) } ?: listOf()
                val selectedFeel = remember {
                    val map = Feel.values().map { it to mutableStateOf(false) }
                    mutableStateMapOf(*(map.toTypedArray()))
                }
                if (feels.isNotEmpty()) setSelectItem(selectedFeel, feels)
                ItemSelectionScreen(
                    moodID = mood,
                    description = stringResource(id = R.string.what_feel),
                    items = selectedFeel,
                    onBack = { controller.popBackStack() },
                    onNext = {
                        if (isItemNotSelect(selectedFeel)) {
                            Toast.makeText(
                                controller.context,
                                R.string.not_select_item_warning,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val events = stackEntry.arguments!!.getStringList(EVENTS)
                                ?.map { Event.valueOf(it) } ?: listOf()
                            navigateToDiary(
                                controller, mood,
                                events, stackEntry.savedStateHandle["feel"]!!
                            )
                        }
                    }
                )
            }
            composable(
                route = EDIT_DIARY.route,
                arguments = EDIT_DIARY.args
            ) { stackEntry ->
                val mood = stackEntry.arguments!!.getFloat(MOOD)
                val now = Date()
                val events = stackEntry.arguments!!.getStringList(EVENTS)
                    ?.map { Event.valueOf(it) } ?: listOf()
                val feels = stackEntry.arguments!!.getStringList(FEELS)
                    ?.map { Feel.valueOf(it) } ?: listOf()
                val diaryTitle = remember { mutableStateOf("") }
                val diaryContent = remember { mutableStateOf("") }
                DiaryScreen(
                    moodID = mood,
                    date = now,
                    diaryTitle = diaryTitle,
                    diaryContent = diaryContent,
                    events = events,
                    feels = feels,
                    onEventClick = {
                        navigateToEventSelection(controller, mood, events, feels)
                    },
                    onFeelClick = {
                        navigateToFeelSelection(controller, mood, events, feels)
                    },
                    onBack = {
                        // pop warning of back to home page
                        onPrevious()
                    },
                    onComplete = {
                        // pop check
                        // store diary data
                        MainScope().run {
                            launch(Dispatchers.IO) {
                                get<DiaryDao>().addDiary(
                                    Diary(
                                        date = now,
                                        currentMood = mood.toInt(),
                                        events = events,
                                        feels = feels,
                                        title = diaryTitle.value,
                                        content = diaryContent.value
                                    )
                                )
                            }
                        }
                        onPrevious()
                    }
                )
            }
        }
    }

}