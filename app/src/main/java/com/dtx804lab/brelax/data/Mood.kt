package com.dtx804lab.brelax.data

import androidx.compose.ui.graphics.Color
import com.dtx804lab.brelax.R
import com.dtx804lab.brelax.ui.theme.Cyan
import com.dtx804lab.brelax.ui.theme.Indigo
import com.dtx804lab.brelax.ui.theme.LightGreen
import com.dtx804lab.brelax.ui.theme.LightRed
import com.dtx804lab.brelax.ui.theme.LightYellow

enum class Mood(val textID: Int, val imageID: Int, val color: Color) {

    VERY_GOOD(R.string.mood_very_good, R.drawable.ic_missing_image, LightYellow),
    GOOD(R.string.mood_good, R.drawable.ic_launcher_background, LightRed),
    NORMAL(R.string.mood_normal, R.drawable.ic_launcher_foreground, LightGreen),
    BAD(R.string.mood_bad, R.drawable.ic_missing_image, Indigo),
    VERY_BAD(R.string.mood_very_bad, R.drawable.ic_missing_image, Cyan);

    companion object {
        fun getMood(id: Float): Mood {
            return Mood.values()[id.toInt().coerceIn(0, Mood.values().size - 1)]
        }

    }

}