package com.dtx804lab.brelax.data

import com.dtx804lab.brelax.R

enum class Event(override val textID: Int, override val imageID: Int): ImageData {

    WORK(R.string.event_work, R.drawable.ic_missing_image),
    FAMILY(R.string.event_family, R.drawable.ic_missing_image),
    FRIEND(R.string.event_friend, R.drawable.ic_missing_image),
    SCHOOL(R.string.event_school, R.drawable.ic_missing_image),
    GAME(R.string.event_game, R.drawable.ic_missing_image),
    FOOD(R.string.event_food, R.drawable.ic_missing_image),
    SLEEP(R.string.event_sleep, R.drawable.ic_missing_image),
    TRAVEL(R.string.event_travel, R.drawable.ic_missing_image),
    WEATHER(R.string.event_weather, R.drawable.ic_missing_image)

}