package com.dtx804lab.brelax.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.dtx804lab.brelax.data.Event
import com.dtx804lab.brelax.data.Feel
import java.util.Date

@Database(
    entities = [Diary::class],
    version = 1,
)
@TypeConverters(DataConverter::class)
abstract class BrelaxDatabase : RoomDatabase() {
    abstract fun dairyDao(): DiaryDao

}

private class DataConverter {

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun dateFromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun eventListToText(list: List<Event>?): String {
        return list?.run {
            joinToString(separator = ",") { it.name }
        } ?: ""
    }

    @TypeConverter
    fun eventListFromText(str: String?): List<Event> {
        return str?.run {
            split(",").map { Event.valueOf(it) }
        } ?: listOf()
    }

    @TypeConverter
    fun feelListToText(list: List<Feel>?): String {
        return list?.run {
            joinToString(separator = ",") { it.name }
        } ?: ""
    }

    @TypeConverter
    fun feelListFromText(str: String?): List<Feel> {
        return str?.run {
            split(",").map { Feel.valueOf(it) }
        } ?: listOf()
    }

}