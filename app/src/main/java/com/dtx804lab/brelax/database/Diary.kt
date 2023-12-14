package com.dtx804lab.brelax.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dtx804lab.brelax.data.Event
import com.dtx804lab.brelax.data.Feel
import java.util.Date

@Entity
data class Diary(
    @PrimaryKey val date: Date,
    val currentMood: Int,
    val events: List<Event>,
    val feels: List<Feel>,
    val title: String,
    val content: String
)
