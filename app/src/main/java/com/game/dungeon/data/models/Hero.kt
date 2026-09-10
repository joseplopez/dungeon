package com.game.dungeon.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
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
    val isInParty: Boolean = false
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
    val maxHp: Int get() = heroClass.baseHp + (level - 1) * (heroClass.baseHp / 10).coerceAtLeast(5)
    val maxMp: Int get() = heroClass.baseMp + (level - 1) * (heroClass.baseMp / 10).coerceAtLeast(2)
    val attack: Int get() = heroClass.baseAttack + (level - 1) * (heroClass.baseAttack / 10).coerceAtLeast(1)
    val magic: Int get() = heroClass.baseMagic + (level - 1) * (heroClass.baseMagic / 10).coerceAtLeast(1)
    val defense: Int get() = heroClass.baseDefense + (level - 1) * (heroClass.baseDefense / 10).coerceAtLeast(1)
    val speed: Int get() = heroClass.baseSpeed + (level - 1) / 5 // Speed increases slowly

    fun calculateStats(equippedItems: List<Item>): Map<String, Int> {
        return mapOf(
            "HP" to maxHp + equippedItems.sumOf { it.hpBonus },
            "ATK" to attack + equippedItems.sumOf { it.attackBonus },
            "DEF" to defense + equippedItems.sumOf { it.defenseBonus },
            "MAG" to magic + equippedItems.sumOf { it.magicBonus }
        )
    }

    companion object {
        val FF_NAMES = listOf("Cecil","Terra","Cloud","Lightning","Tidus","Noctis",
            "Zidane","Squall","Bartz","Firion","Onion","Rosa","Rydia","Aerith","Yuna",
            "Celes","Tifa","Garnet","Rinoa","Penelo","Vivi","Steiner","Fran","Balthier",
            "Edge","Kain","Locke","Sabin","Edgar","Cyan","Gau","Mog","Umaro","Gogo")

        fun create(jobClass: JobClass): Hero {
            val name = FF_NAMES.random()
            return Hero(
                heroClass = jobClass, name = name,
                currentHp = jobClass.baseHp,
                currentMp = jobClass.baseMp,
                aiPriority = jobClass.defaultPriority
            )
        }
    }
}
