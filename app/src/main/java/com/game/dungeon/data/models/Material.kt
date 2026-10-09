package com.game.dungeon.data.models

import androidx.annotation.StringRes
import com.game.dungeon.R
import java.util.UUID

enum class MaterialCategory {
    ORE,
    MONSTER_PART,
    ESSENCE,
    BOSS_TROPHY
}

data class Material(
    val id: String,
    @get:StringRes val nameRes: Int,
    val category: MaterialCategory,
    val rarity: Rarity = Rarity.COMMON,
    @get:StringRes val descriptionRes: Int = R.string.mat_unknown_material_desc,
    val emoji: String = "📦",
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val magicBonus: Int = 0,
    val hpBonus: Int = 0,
    val statBonus: Int = 0,
) {
    val powerScore: Int
        get() = attackBonus + defenseBonus + magicBonus + (hpBonus / 5) + statBonus
}

data class SocketSlot(
    val id: String = UUID.randomUUID().toString(),
    val socketedGem: Material? = null,
) {
    val isFilled: Boolean get() = socketedGem != null
    val gem: Material? get() = socketedGem
    val powerBonus: Int get() = socketedGem?.powerScore ?: 0
}

data class MaterialInventoryItem(
    val material: Material,
    val amount: Int
)

object MaterialCatalog {
    private val materials = mapOf(
        // Ores
        "iron_ore" to Material("iron_ore", R.string.mat_iron_ore_name, MaterialCategory.ORE, Rarity.COMMON, R.string.mat_iron_ore_desc, "🪨"),
        "mithril_ore" to Material("mithril_ore", R.string.mat_mithril_ore_name, MaterialCategory.ORE, Rarity.RARE, R.string.mat_mithril_ore_desc, "🪙"),
        "adamantite_ore" to Material("adamantite_ore", R.string.mat_adamantite_ore_name, MaterialCategory.ORE, Rarity.EPIC, R.string.mat_adamantite_ore_desc, "💎"),
        "orichalcum_ore" to Material("orichalcum_ore", R.string.mat_orichalcum_ore_name, MaterialCategory.ORE, Rarity.LEGENDARY, R.string.mat_orichalcum_ore_desc, "✨"),

        // Monster Parts
        "beast_fang" to Material("beast_fang", R.string.mat_beast_fang_name, MaterialCategory.MONSTER_PART, Rarity.COMMON, R.string.mat_beast_fang_desc, "🦷"),
        "monster_bone" to Material("monster_bone", R.string.mat_monster_bone_name, MaterialCategory.MONSTER_PART, Rarity.COMMON, R.string.mat_monster_bone_desc, "🦴"),
        "dragon_scale" to Material("dragon_scale", R.string.mat_dragon_scale_name, MaterialCategory.MONSTER_PART, Rarity.EPIC, R.string.mat_dragon_scale_desc, "🐉"),
        "demon_horn" to Material("demon_horn", R.string.mat_demon_horn_name, MaterialCategory.MONSTER_PART, Rarity.RARE, R.string.mat_demon_horn_desc, "😈"),

        // Essences
        "fire_essence" to Material("fire_essence", R.string.mat_fire_essence_name, MaterialCategory.ESSENCE, Rarity.COMMON, R.string.mat_fire_essence_desc, "🔥"),
        "ice_essence" to Material("ice_essence", R.string.mat_ice_essence_name, MaterialCategory.ESSENCE, Rarity.COMMON, R.string.mat_ice_essence_desc, "❄️"),
        "lightning_essence" to Material("lightning_essence", R.string.mat_lightning_essence_name, MaterialCategory.ESSENCE, Rarity.COMMON, R.string.mat_lightning_essence_desc, "⚡"),
        "dark_essence" to Material("dark_essence", R.string.mat_dark_essence_name, MaterialCategory.ESSENCE, Rarity.RARE, R.string.mat_dark_essence_desc, "🖤"),

        // Gems
        "ruby_gem_1" to Material("ruby_gem_1", R.string.mat_ruby_gem_1_name, MaterialCategory.ESSENCE, Rarity.COMMON, R.string.mat_ruby_gem_1_desc, "🔴", attackBonus = 5, statBonus = 5),
        "ruby_gem_2" to Material("ruby_gem_2", R.string.mat_ruby_gem_2_name, MaterialCategory.ESSENCE, Rarity.RARE, R.string.mat_ruby_gem_2_desc, "🔴", attackBonus = 15, statBonus = 15),
        "ruby_gem_3" to Material("ruby_gem_3", R.string.mat_ruby_gem_3_name, MaterialCategory.ESSENCE, Rarity.EPIC, R.string.mat_ruby_gem_3_desc, "🔴", attackBonus = 35, statBonus = 35),
        "sapphire_gem_1" to Material("sapphire_gem_1", R.string.mat_sapphire_gem_1_name, MaterialCategory.ESSENCE, Rarity.COMMON, R.string.mat_sapphire_gem_1_desc, "🔵", magicBonus = 5, statBonus = 5),
        "sapphire_gem_2" to Material("sapphire_gem_2", R.string.mat_sapphire_gem_2_name, MaterialCategory.ESSENCE, Rarity.RARE, R.string.mat_sapphire_gem_2_desc, "🔵", magicBonus = 15, statBonus = 15),
        "sapphire_gem_3" to Material("sapphire_gem_3", R.string.mat_sapphire_gem_3_name, MaterialCategory.ESSENCE, Rarity.EPIC, R.string.mat_sapphire_gem_3_desc, "🔵", magicBonus = 35, statBonus = 35),
        "emerald_gem_1" to Material("emerald_gem_1", R.string.mat_emerald_gem_1_name, MaterialCategory.ESSENCE, Rarity.COMMON, R.string.mat_emerald_gem_1_desc, "🟢", defenseBonus = 5, statBonus = 5),
        "emerald_gem_2" to Material("emerald_gem_2", R.string.mat_emerald_gem_2_name, MaterialCategory.ESSENCE, Rarity.RARE, R.string.mat_emerald_gem_2_desc, "🟢", defenseBonus = 15, statBonus = 15),
        "emerald_gem_3" to Material("emerald_gem_3", R.string.mat_emerald_gem_3_name, MaterialCategory.ESSENCE, Rarity.EPIC, R.string.mat_emerald_gem_3_desc, "🟢", defenseBonus = 35, statBonus = 35),
        "topaz_gem_1" to Material("topaz_gem_1", R.string.mat_topaz_gem_1_name, MaterialCategory.ESSENCE, Rarity.COMMON, R.string.mat_topaz_gem_1_desc, "💛", hpBonus = 25, statBonus = 5),
        "topaz_gem_2" to Material("topaz_gem_2", R.string.mat_topaz_gem_2_name, MaterialCategory.ESSENCE, Rarity.RARE, R.string.mat_topaz_gem_2_desc, "💛", hpBonus = 75, statBonus = 15),
        "topaz_gem_3" to Material("topaz_gem_3", R.string.mat_topaz_gem_3_name, MaterialCategory.ESSENCE, Rarity.EPIC, R.string.mat_topaz_gem_3_desc, "💛", hpBonus = 175, statBonus = 35),

        // Boss Trophies
        "boss_trophy_1" to Material("boss_trophy_1", R.string.mat_boss_trophy_1_name, MaterialCategory.BOSS_TROPHY, Rarity.RARE, R.string.mat_boss_trophy_1_desc, "👑"),
        "boss_trophy_2" to Material("boss_trophy_2", R.string.mat_boss_trophy_2_name, MaterialCategory.BOSS_TROPHY, Rarity.EPIC, R.string.mat_boss_trophy_2_desc, "💀"),
        "boss_trophy_3" to Material("boss_trophy_3", R.string.mat_boss_trophy_3_name, MaterialCategory.BOSS_TROPHY, Rarity.LEGENDARY, R.string.mat_boss_trophy_3_desc, "👿")
    )

    fun getMaterial(id: String): Material {
        return materials[id] ?: Material(
            id = id,
            nameRes = R.string.mat_unknown_material_name,
            descriptionRes = R.string.mat_unknown_material_desc,
            category = MaterialCategory.ORE
        )
    }

    fun getAllKnownMaterials(): List<Material> = materials.values.toList()
}
