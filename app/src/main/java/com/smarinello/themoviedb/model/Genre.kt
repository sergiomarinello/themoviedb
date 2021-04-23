package com.smarinello.themoviedb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the [Genre] data.
 */
@Entity(tableName = "genre_list")
data class Genre(
    @SerializedName("id")
    @PrimaryKey val id: Int,

    @SerializedName("name")
    val name: String
)
