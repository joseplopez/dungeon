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

    @TypeConverter
    fun fromHeroClassMap(value: Map<com.game.dungeon.data.models.HeroClass, Int>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toHeroClassMap(value: String): Map<com.game.dungeon.data.models.HeroClass, Int> {
        val type = object : TypeToken<Map<com.game.dungeon.data.models.HeroClass, Int>>() {}.type
        return gson.fromJson(value, type)
    }

    // Reuse same converters for EXP map as it's the same type Map<HeroClass, Int>
    @TypeConverter
    fun fromPetTypeSet(value: Set<com.game.dungeon.data.models.PetType>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toPetTypeSet(value: String?): Set<com.game.dungeon.data.models.PetType>? {
        val type = object : TypeToken<Set<com.game.dungeon.data.models.PetType>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromPetType(value: com.game.dungeon.data.models.PetType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toPetType(value: String?): com.game.dungeon.data.models.PetType? {
        return value?.let { com.game.dungeon.data.models.PetType.valueOf(it) }
    }
}
