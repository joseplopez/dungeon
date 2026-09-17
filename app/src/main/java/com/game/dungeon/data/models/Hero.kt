package com.game.dungeon.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.game.dungeon.R
import java.util.UUID

@Entity(tableName = "heroes")
data class Hero(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val heroClass: HeroClass,
    val name: String,             // random FF-style name from a list
    var currentHp: Int,
    var currentMp: Int,
    var level: Int = 1,           // Starting level is 1
    var exp: Int = 0,             // Current experience
    var expToNextLevel: Int = 10, // XP needed for next level
    var abilityCharge: Int = 0,   // 0-3, triggers special at 3
    val aiPriority: AIPriority,   // player-set per hero
    var weaponId: String? = null,
    var armorId: String? = null,
    var shieldId: String? = null,
    var accessory1Id: String? = null,
    var accessory2Id: String? = null,
    val isInParty: Boolean = false,
    val partyPosition: Int = 0 // Position in the party list (0-indexed)
) {
    @Ignore var attackBonus: Int = 0
    @Ignore var defenseBonus: Int = 0
    @Ignore var magicBonus: Int = 0
    @Ignore var hpBonus: Int = 0
    @Ignore var mpBonus: Int = 0
    @Ignore var critChance: Int = 5
    @Ignore var critDamage: Int = 50

    @get:Ignore
    val isAlive: Boolean get() = currentHp > 0

    @get:Ignore
    val nickname: String get() = name

    val hasSpecialAbility: Boolean get() = heroClass != HeroClass.FREELANCER

    val masteryLevel: Int get() = when { level >= 50 -> 50; level >= 25 -> 25; else -> level }
    val hasGroupHeal: Boolean get() = heroClass == HeroClass.WHITE_MAGE && level >= 10
    val hasMagicBoost: Boolean get() = heroClass == HeroClass.BLACK_MAGE && level >= 10
    val hasRansack: Boolean get() = heroClass == HeroClass.THIEF && level >= 10

    /**
     * Centralized experience logic. Returns true if leveled up.
     */
    fun addExperience(amount: Int): Boolean {
        val wasFullHp = currentHp >= maxHp
        val wasFullMp = currentMp >= maxMp

        exp += amount
        var leveledUp = false
        while (exp >= expToNextLevel) {
            exp -= expToNextLevel
            level++
            expToNextLevel = (expToNextLevel * 1.5).toInt()
            leveledUp = true
        }

        if (leveledUp) {
            if (wasFullHp) currentHp = maxHp
            if (wasFullMp) currentMp = maxMp
        }
        return leveledUp
    }

    // Dynamic stats based on level
    val baseMaxHp: Int get() {
        if (heroClass == HeroClass.ONION_KNIGHT) {
            return if (level < 90) heroClass.baseHp + (level - 1) * 2 
            else 500 + (level - 90) * 150 // Massive spike
        }
        return heroClass.baseHp + (level - 1) * (heroClass.baseHp / 10).coerceAtLeast(5)
    }
    val baseMaxMp: Int get() {
        if (heroClass == HeroClass.ONION_KNIGHT) {
            return if (level < 90) heroClass.baseMp + (level - 1) 
            else 200 + (level - 90) * 50
        }
        return heroClass.baseMp + (level - 1) * (heroClass.baseMp / 10).coerceAtLeast(2)
    }
    val baseAttack: Int get() {
        if (heroClass == HeroClass.ONION_KNIGHT) {
            return if (level < 90) heroClass.baseAttack + (level - 1) 
            else 100 + (level - 90) * 30
        }
        return heroClass.baseAttack + (level - 1) * (heroClass.baseAttack / 10).coerceAtLeast(1)
    }
    val baseMagic: Int get() {
        if (heroClass == HeroClass.ONION_KNIGHT) {
            return if (level < 90) heroClass.baseMagic + (level - 1) 
            else 100 + (level - 90) * 30
        }
        return heroClass.baseMagic + (level - 1) * (heroClass.baseMagic / 10).coerceAtLeast(1)
    }
    val baseDefense: Int get() {
        if (heroClass == HeroClass.ONION_KNIGHT) {
            return if (level < 90) heroClass.baseDefense + (level - 1) 
            else 100 + (level - 90) * 30
        }
        return heroClass.baseDefense + (level - 1) * (heroClass.baseDefense / 10).coerceAtLeast(1)
    }

    val maxHp: Int get() = baseMaxHp + hpBonus
    val maxMp: Int get() = baseMaxMp + mpBonus
    val attack: Int get() = baseAttack + attackBonus
    val magic: Int get() = baseMagic + magicBonus
    val defense: Int get() = baseDefense + defenseBonus
    val speed: Int get() = heroClass.baseSpeed + (level - 1) / 5 // Speed increases slowly

    fun calculateStats(equippedItems: List<Item>, relicBonuses: RelicBonuses? = null): Map<String, Int> {
        val masteryHp = relicBonuses?.getMasteryBonus(heroClass, StatType.HP) ?: 0
        val masteryAtk = relicBonuses?.getMasteryBonus(heroClass, StatType.ATTACK) ?: 0
        val masteryDef = relicBonuses?.getMasteryBonus(heroClass, StatType.DEFENSE) ?: 0
        val masteryMag = relicBonuses?.getMasteryBonus(heroClass, StatType.MAGIC) ?: 0
        val masteryMp = relicBonuses?.getMasteryBonus(heroClass, StatType.MP) ?: 0
        val masteryCritChance = relicBonuses?.getMasteryBonus(heroClass, StatType.CRIT_CHANCE) ?: 0
        val masteryCritDmg = relicBonuses?.getMasteryBonus(heroClass, StatType.CRIT_DAMAGE) ?: 0

        val petCritChance = relicBonuses?.petCritChanceBonus ?: 0
        val petCritDmg = relicBonuses?.petCritDamageBonus ?: 0

        // Blue Mage Lore Bonus: +2 to all stats per unique boss defeated
        val loreBonus = if (heroClass == HeroClass.BLUE_MAGE) (relicBonuses?.bossesDefeatedCount ?: 0) * 2 else 0

        return mapOf(
            "HP" to baseMaxHp + equippedItems.sumOf { it.hpBonus } + (relicBonuses?.hpBonus ?: 0) + masteryHp + (loreBonus * 5),
            "MP" to baseMaxMp + equippedItems.sumOf { it.mpBonus } + (relicBonuses?.mpBonus ?: 0) + masteryMp + (loreBonus * 2),
            "ATK" to baseAttack + equippedItems.sumOf { it.attackBonus } + (relicBonuses?.attackBonus ?: 0) + masteryAtk + loreBonus,
            "DEF" to baseDefense + equippedItems.sumOf { it.defenseBonus } + (relicBonuses?.defenseBonus ?: 0) + masteryDef + loreBonus,
            "MAG" to baseMagic + equippedItems.sumOf { it.magicBonus } + (relicBonuses?.magicBonus ?: 0) + masteryMag + loreBonus,
            "CRIT_CHANCE" to 5 + equippedItems.sumOf { it.critChanceBonus } + (relicBonuses?.critChanceBonus ?: 0) + masteryCritChance + petCritChance,
            "CRIT_DAMAGE" to 50 + equippedItems.sumOf { it.critDamageBonus } + (relicBonuses?.critDamageBonus ?: 0) + masteryCritDmg + petCritDmg
        )
    }

    companion object {
        fun create(heroClass: HeroClass, context: android.content.Context): Hero {
            val names = context.resources.getStringArray(R.array.hero_names)
            val name = names.random()
            return Hero(
                heroClass = heroClass, name = name,
                currentHp = heroClass.baseHp,
                currentMp = heroClass.baseMp,
                aiPriority = heroClass.defaultPriority
            )
        }
    }
}
