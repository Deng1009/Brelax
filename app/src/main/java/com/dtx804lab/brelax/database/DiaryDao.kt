package com.dtx804lab.brelax.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DiaryDao {

    @Query("SELECT date FROM diary")
    fun getAllTime(): List<Long>

    @Insert
    fun addDiary(diary: Diary)

}