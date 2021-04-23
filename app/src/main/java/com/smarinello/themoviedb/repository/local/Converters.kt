package com.smarinello.themoviedb.repository.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smarinello.themoviedb.model.Genre
import java.util.Collections

private const val STRING_SEPARATOR = ","

/**
 * Converters for room database.
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromString(genreIds: String?): List<Int> {
        var list: List<Int> = ArrayList()
        genreIds?.let {
            list = genreIds.split(STRING_SEPARATOR).map { it.toInt() }
        }
        return list
    }

    @TypeConverter
    fun fromList(list: List<Int>): String = list.joinToString(STRING_SEPARATOR)

    @TypeConverter
    fun stringToList(data: String?): List<Genre> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<Genre>): String = gson.toJson(someObjects)
}
