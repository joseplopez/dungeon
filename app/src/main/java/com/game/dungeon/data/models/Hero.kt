package com.game.dungeon.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.game.dungeon.R
import java.util.UUID

@Entity(tableName = "heroes")
data class Hero(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val heroClass: JobClass,
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
    var attackBonus: Int = 0,
    var defenseBonus: Int = 0,
    var magicBonus: Int = 0,
    var hpBonus: Int = 0,
    var mpBonus: Int = 0,
    var critChance: Int = 5,
    var critDamage: Int = 50,
    val isInParty: Boolean = false,
    val partyPosition: Int = 0 // Position in the party list (0-indexed)
) {
    @get:Ignore
    val isAlive: Boolean get() = currentHp > 0

    @get:Ignore
    val nickname: String get() = name

    val hasSpecialAbility: Boolean get() = heroClass != JobClass.FREELANCER

    val masteryLevel: Int get() = when { level >= 50 -> 50; level >= 25 -> 25; else -> level }
    val hasGroupHeal: Boolean get() = heroClass == JobClass.WHITE_MAGE && level >= 10
    val hasMagicBoost: Boolean get() = heroClass == JobClass.BLACK_MAGE && level >= 10
    val hasRansack: Boolean get() = heroClass == JobClass.THIEF && level >= 10

    // Dynamic stats based on level
    val baseMaxHp: Int get() = heroClass.baseHp + (level - 1) * (heroClass.baseHp / 10).coerceAtLeast(5)
    val baseMaxMp: Int get() = heroClass.baseMp + (level - 1) * (heroClass.baseMp / 10).coerceAtLeast(2)
    val baseAttack: Int get() = heroClass.baseAttack + (level - 1) * (heroClass.baseAttack / 10).coerceAtLeast(1)
    val baseMagic: Int get() = heroClass.baseMagic + (level - 1) * (heroClass.baseMagic / 10).coerceAtLeast(1)
    val baseDefense: Int get() = heroClass.baseDefense + (level - 1) * (heroClass.baseDefense / 10).coerceAtLeast(1)

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

        // Use base values + passed parameters ONLY to avoid double-counting baked-in bonuses
        val baseMaxHp = heroClass.baseHp + (level - 1) * (heroClass.baseHp / 10).coerceAtLeast(5)
        val baseMaxMp = heroClass.baseMp + (level - 1) * (heroClass.baseMp / 10).coerceAtLeast(2)
        
        return mapOf(
            "HP" to baseMaxHp + equippedItems.sumOf { it.hpBonus } + (relicBonuses?.hpBonus ?: 0) + masteryHp,
            "MP" to baseMaxMp + equippedItems.sumOf { it.mpBonus } + (relicBonuses?.mpBonus ?: 0) + masteryMp,
            "ATK" to baseAttack + equippedItems.sumOf { it.attackBonus } + (relicBonuses?.attackBonus ?: 0) + masteryAtk,
            "DEF" to baseDefense + equippedItems.sumOf { it.defenseBonus } + (relicBonuses?.defenseBonus ?: 0) + masteryDef,
            "MAG" to baseMagic + equippedItems.sumOf { it.magicBonus } + (relicBonuses?.magicBonus ?: 0) + masteryMag,
            "CRIT_CHANCE" to 5 + equippedItems.sumOf { it.critChanceBonus } + (relicBonuses?.critChanceBonus ?: 0) + masteryCritChance + petCritChance,
            "CRIT_DAMAGE" to 50 + equippedItems.sumOf { it.critDamageBonus } + (relicBonuses?.critDamageBonus ?: 0) + masteryCritDmg + petCritDmg
        )
    }

    companion object {
        fun create(jobClass: JobClass, context: android.content.Context): Hero {
            val names = context.resources.getStringArray(R.array.hero_names)
            val name = names.random()
            return Hero(
                heroClass = jobClass, name = name,
                currentHp = jobClass.baseHp,
                currentMp = jobClass.baseMp,
                aiPriority = jobClass.defaultPriority
            )
        }
    }
}
