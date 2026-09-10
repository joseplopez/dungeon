package com.game.dungeon.data.db

import androidx.room.TypeConverter
import com.game.dungeon.data.models.CrystalColor
import com.game.dungeon.data.models.JobClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCrystalMap(value: Map<CrystalColor, Boolean>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCrystalMap(value: String): Map<CrystalColor, Boolean> {
        val type = object : TypeToken<Map<CrystalColor, Boolean>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromJobSet(value: Set<JobClass>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toJobSet(value: String): Set<JobClass> {
        val type = object : TypeToken<Set<JobClass>>() {}.type
        return gson.fromJson(value, type)
    }
}
