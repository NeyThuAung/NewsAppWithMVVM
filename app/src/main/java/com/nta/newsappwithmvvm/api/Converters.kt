package com.nta.newsappwithmvvm.api

import androidx.room.TypeConverter
import com.nta.newsappwithmvvm.modals.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name.toString()
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}