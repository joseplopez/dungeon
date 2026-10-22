package com.game.dungeon.data.models

import com.game.dungeon.R

enum class MythicSet(
    val id: String,
    val targetClass: HeroClass,
    val nameRes: Int,
    val requiredPieces: Int = 3,
    val statMultipliers: Map<String, Float>,
    val allowedSlots: List<ItemSlot> // NEW: Restricts sets to their exact 3 lore pieces
) {
    CRYSTAL("set_crystal", HeroClass.WARRIOR, R.string.set_crystal, 3, mapOf("ATK_MULT" to 0.4f, "HP_MULT" to 0.25f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.SHIELD)),
    MAGUS("set_magus", HeroClass.BLACK_MAGE, R.string.set_magus, 3, mapOf("MAG_MULT" to 0.4f, "CRIT_CHANCE_ADD" to 15f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    DEVOUT("set_devout", HeroClass.WHITE_MAGE, R.string.set_devout, 3, mapOf("MAG_MULT" to 0.3f, "MP_MULT" to 0.4f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    SHADOW("set_shadow", HeroClass.NINJA, R.string.set_shadow, 3, mapOf("ATK_MULT" to 0.25f, "CRIT_CHANCE_ADD" to 20f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    CHAKRA("set_chakra", HeroClass.MONK, R.string.set_chakra, 3, mapOf("ATK_MULT" to 0.35f, "CRIT_DMG_ADD" to 50f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    WYVERN("set_wyvern", HeroClass.DRAGOON, R.string.set_wyvern, 3, mapOf("ATK_MULT" to 0.3f, "DEF_MULT" to 0.15f, "CRIT_DMG_ADD" to 30f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.SHIELD)),
    CRIMSON("set_crimson", HeroClass.RED_MAGE, R.string.set_crimson, 3, mapOf("ATK_MULT" to 0.25f, "MAG_MULT" to 0.25f, "MP_MULT" to 0.2f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    EVOKER("set_evoker", HeroClass.SUMMONER, R.string.set_evoker, 3, mapOf("MAG_MULT" to 0.45f, "HP_MULT" to -0.1f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    APOLLO("set_apollo", HeroClass.BARD, R.string.set_apollo, 3, mapOf("HP_MULT" to 0.3f, "MP_MULT" to 0.5f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    GENJI("set_genji", HeroClass.SAMURAI, R.string.set_genji, 3, mapOf("ATK_MULT" to 0.4f, "CRIT_DMG_ADD" to 30f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    AEGIS("set_aegis", HeroClass.PALADIN, R.string.set_aegis, 3, mapOf("HP_MULT" to 0.35f, "DEF_MULT" to 0.35f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.SHIELD)),
    ONION("set_onion", HeroClass.ONION_KNIGHT, R.string.set_onion, 3, mapOf("ATK_MULT" to 0.3f, "DEF_MULT" to 0.3f, "MAG_MULT" to 0.3f, "HP_MULT" to 0.3f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.SHIELD)),
    ADVENTURER("set_adventurer", HeroClass.FREELANCER, R.string.set_adventurer, 3, mapOf("HP_MULT" to 0.3f, "ATK_MULT" to 0.3f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    LIONHEART("set_lionheart", HeroClass.KNIGHT, R.string.set_lionheart, 3, mapOf("ATK_MULT" to 0.35f, "DEF_MULT" to 0.4f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.SHIELD)),
    MIRAGE("set_mirage", HeroClass.THIEF, R.string.set_mirage, 3, mapOf("ATK_MULT" to 0.2f, "CRIT_CHANCE_ADD" to 25f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    MIMIC("set_mimic", HeroClass.MIME, R.string.set_mimic, 3, mapOf("HP_MULT" to 0.2f, "MAG_MULT" to 0.2f, "ATK_MULT" to 0.2f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    REAPER("set_reaper", HeroClass.NECROMANCER, R.string.set_reaper, 3, mapOf("MAG_MULT" to 0.5f, "HP_MULT" to -0.15f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY)),
    AZURE("set_azure", HeroClass.BLUE_MAGE, R.string.set_azure, 3, mapOf("HP_MULT" to 0.4f, "MAG_MULT" to 0.3f), listOf(ItemSlot.WEAPON, ItemSlot.ARMOR, ItemSlot.ACCESSORY));

    companion object {
        fun fromId(id: String?): MythicSet? = entries.find { it.id == id }
    }
}
