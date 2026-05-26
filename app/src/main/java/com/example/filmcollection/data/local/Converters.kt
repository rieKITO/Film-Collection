package com.example.filmcollection.data.local

import androidx.room.TypeConverter
import com.example.filmcollection.model.WatchStatus

class Converters {
    @TypeConverter
    fun watchStatusToString(status: WatchStatus): String = status.name

    @TypeConverter
    fun stringToWatchStatus(value: String): WatchStatus =
        runCatching { WatchStatus.valueOf(value) }.getOrDefault(WatchStatus.NOT_WATCHED)
}
