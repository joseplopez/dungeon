package com.game.dungeon.data.models

import android.content.Context
import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.game.dungeon.R
import java.util.UUID
import kotlin.math.sqrt
import kotlin.random.Random

enum class ItemSlot(@get:StringRes val nameRes: Int) {
    WEAPON(R.string.slot_weapon),
    ARMOR(R.string.slot_armor),
    SHIELD(R.string.slot_shield),
    ACCESSORY(R.string.slot_accessory)
}

enum class Rarity(val color: Long) {
    COMMON(0xFF888888),
    RARE(0xFF4488FF),
    EPIC(0xFFAA44FF),
    LEGENDARY(0xFFFFAA00),
    MYTHIC(0xFFFF2255)
}

private data class ItemCategorySpec(
    val nameRes: Int,
    val emoji: String,
    val isMagicalWeapon: Boolean = false
)

@Entity(tableName = "items")
data class Item(
    @PrimaryKey val id: String,
    val name: String,
    val slot: ItemSlot,
    val rarity: Rarity,
    val attackBonus: Long = 0,
    val defenseBonus: Long = 0,
    val magicBonus: Long = 0,
    val hpBonus: Long = 0,
    val mpBonus: Long = 0,
    val critChanceBonus: Int = 0,
    val critDamageBonus: Long = 0,
    val emoji: String,
    val floorFound: Int,
    val ownerId: String? = null, // To track who is wearing it, or if it's in inventory
    val enhancementLevel: Int = 0,
    val sockets: List<SocketSlot> = emptyList(),
    @ColumnInfo(defaultValue = "NULL") val setId: String? = null,
    val isLocked: Boolean = false
) {
    val sellValue: Long get() = (floorFound * 5L + rarity.ordinal * 20L).coerceAtLeast(5L)

    val basePowerScore: Long get() = attackBonus + defenseBonus + magicBonus + (hpBonus / 5) + (mpBonus / 2) + (critChanceBonus * 2) + (critDamageBonus / 2)

    val socketBonus: Int get() = sockets.sumOf { it.socketedGem?.powerScore ?: 0 }

    val powerScore: Double get() = (basePowerScore * (1.0 + enhancementLevel * 0.05)) + socketBonus

    val effectiveAttackBonus: Double get() = (attackBonus * (1.0 + enhancementLevel * 0.05)) * (1.0 + sockets.sumOf { it.socketedGem?.attackBonus ?: 0 } / 100.0)
    val effectiveDefenseBonus: Double get() = (defenseBonus * (1.0 + enhancementLevel * 0.05)) * (1.0 + sockets.sumOf { it.socketedGem?.defenseBonus ?: 0 } / 100.0)
    val effectiveMagicBonus: Double get() = (magicBonus * (1.0 + enhancementLevel * 0.05)) * (1.0 + sockets.sumOf { it.socketedGem?.magicBonus ?: 0 } / 100.0)
    val effectiveHpBonus: Double get() = (hpBonus * (1.0 + enhancementLevel * 0.05)) * (1.0 + sockets.sumOf { it.socketedGem?.hpBonus ?: 0 } / 100.0)

    companion object {
        fun random(
            floor: Int,
            context: Context,
            relicBonuses: RelicBonuses? = null,
            minRarity: Rarity = Rarity.COMMON
        ): Item {
            return random(floor, context, relicBonuses, minRarity, dimension = 1)
        }

        fun random(
            floor: Int,
            context: Context,
            relicBonuses: RelicBonuses? = null,
            minRarity: Rarity = Rarity.COMMON,
            dimension: Int = 1,
            forcedSlot: ItemSlot? = null
        ): Item {
            val msLevel = relicBonuses?.magicShopLevel ?: 0
            val mythicDropBoost = ((relicBonuses?.mythicDropBonus ?: 0f) * 100).toInt()
            val dimRarityBoost = (dimension - 1) * 2
            val totalBoost = msLevel + dimRarityBoost + mythicDropBoost

            val roll = (1..100).random()
            val rarity = calculateRarity(roll, totalBoost, minRarity)
            
            val slot = forcedSlot ?: ItemSlot.entries.random()
            val id = UUID.randomUUID().toString()
            val gearCategory = getGearCategory(slot, dimension)

            val categoryName = try { context.getString(gearCategory.nameRes) } catch (_: Exception) { null } ?: "Equipment"
            val emoji = gearCategory.emoji

            val rarityRes = when (rarity) {
                Rarity.COMMON -> R.string.item_rarity_common
                Rarity.RARE -> R.string.item_rarity_rare
                Rarity.EPIC -> R.string.item_rarity_epic
                Rarity.LEGENDARY -> R.string.item_rarity_legendary
                Rarity.MYTHIC -> R.string.item_rarity_mythic
            }
            
            val rarityStr = try { context.getString(rarityRes) } catch (_: Exception) { null } ?: rarity.name
            val fullName = try { context.getString(R.string.item_name_template, rarityStr, categoryName) } catch (_: Exception) { null } ?: "$rarityStr $categoryName"
            
            val bonusMult = when (rarity) {
                Rarity.COMMON -> 1.0
                Rarity.RARE -> 1.5
                Rarity.EPIC -> 2.5
                Rarity.LEGENDARY -> 4.0
                Rarity.MYTHIC -> 10.0
            }
            
            val mythicStatMult = if (rarity == Rarity.LEGENDARY || rarity == Rarity.MYTHIC) (1f + (relicBonuses?.mythicStatBonus ?: 0f)) else 1f
            val statBonus = 1.0 + (relicBonuses?.itemStatBonus ?: 0.0).toDouble()
            val dimBonus = 1 + (dimension - 1) * 0.12
            val finalMult = bonusMult * statBonus * dimBonus * mythicStatMult

            // Soften floor scaling for item generation above floor 200 to prevent multi-million stat inflations
            val effectiveItemFloor = if (floor <= 200) {
                floor.toDouble()
            } else {
                200 + sqrt((floor - 200).toDouble()) * 8
            }

            val (critChance, critDmg) = calculateCritBonuses(rarity)
            val baseWeaponStat = ((2.0 + effectiveItemFloor / 8.0) * finalMult)

            val atkBonus = if (slot == ItemSlot.WEAPON && !gearCategory.isMagicalWeapon) baseWeaponStat else 0
            val defBonus = if (slot == ItemSlot.ARMOR || slot == ItemSlot.SHIELD) ((2f + effectiveItemFloor / 6f) * finalMult) else 0
            val magBonus = when {
                slot == ItemSlot.WEAPON && gearCategory.isMagicalWeapon -> baseWeaponStat
                slot == ItemSlot.ACCESSORY -> ((1f + effectiveItemFloor / 12f) * finalMult)
                else -> 0
            }

            val setId = if (rarity == Rarity.MYTHIC) {
                MythicSet.entries.random().id
            } else {
                null
            }

            return Item(
                id = id,
                name = fullName,
                slot = slot,
                rarity = rarity,
                attackBonus = atkBonus.toLong(),
                defenseBonus = defBonus.toLong(),
                magicBonus = magBonus.toLong(),
                hpBonus = ((5.0 + effectiveItemFloor * 0.5) * finalMult).toLong(),
                mpBonus = if (slot == ItemSlot.ACCESSORY) ((2 + effectiveItemFloor / 15) * finalMult).toLong() else 0,
                critChanceBonus = critChance,
                critDamageBonus = critDmg.toLong(),
                emoji = emoji,
                floorFound = floor,
                setId = setId
            )
        }

        private fun calculateRarity(roll: Int, totalBoost: Int, minRarity: Rarity): Rarity {
            var rarity = when {
                roll >= (99 - totalBoost) -> Rarity.LEGENDARY
                roll >= (91 - totalBoost * 2) -> Rarity.EPIC
                roll >= (71 - totalBoost * 3) -> Rarity.RARE
                else -> Rarity.COMMON
            }
            if (rarity.ordinal < minRarity.ordinal) {
                rarity = minRarity
            }
            return rarity
        }

        private fun calculateCritBonuses(rarity: Rarity): Pair<Int, Int> {
            var critChance = 0
            var critDmg = 0
            if (rarity.ordinal >= Rarity.RARE.ordinal) {
                if (Random.nextInt(100) < 30) {
                    critChance = when (rarity) {
                        Rarity.RARE -> Random.nextInt(2, 5)
                        Rarity.EPIC -> Random.nextInt(4, 8)
                        Rarity.LEGENDARY, Rarity.MYTHIC -> Random.nextInt(6, 11)
                        else -> 0
                    }
                }
                if (Random.nextInt(100) < 30) {
                    critDmg = when (rarity) {
                        Rarity.RARE -> Random.nextInt(5, 11)
                        Rarity.EPIC -> Random.nextInt(10, 19)
                        Rarity.LEGENDARY, Rarity.MYTHIC -> Random.nextInt(18, 31)
                        else -> 0
                    }
                }
            }
            return Pair(critChance, critDmg)
        }

        private fun getGearCategory(slot: ItemSlot, dimension: Int): ItemCategorySpec {
            return when (slot) {
                ItemSlot.WEAPON -> getWeaponCategory(dimension)
                ItemSlot.ARMOR -> getArmorCategory(dimension)
                ItemSlot.SHIELD -> getShieldCategory(dimension)
                ItemSlot.ACCESSORY -> getAccessoryCategory(dimension)
            }
        }

        private fun getWeaponCategory(dimension: Int): ItemCategorySpec {
            return when (dimension) {
                1 -> listOf(
                    ItemCategorySpec(R.string.item_cat_sword, "⚔️"),
                    ItemCategorySpec(R.string.item_cat_dagger, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_staff, "🪄", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_wooden_wand, "🪄", isMagicalWeapon = true)
                ).random()
                2 -> listOf(
                    ItemCategorySpec(R.string.item_cat_battle_axe, "🪓"),
                    ItemCategorySpec(R.string.item_cat_hunting_bow, "🏹"),
                    ItemCategorySpec(R.string.item_cat_knight_lance, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_mithril_rod, "🪄", isMagicalWeapon = true)
                ).random()
                3 -> listOf(
                    ItemCategorySpec(R.string.item_cat_katana, "⚔️"),
                    ItemCategorySpec(R.string.item_cat_greatsword, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_scythe, "🌾"),
                    ItemCategorySpec(R.string.item_cat_starlight_wand, "🪄", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_crystal_scepter, "🔮", isMagicalWeapon = true)
                ).random()
                4 -> listOf(
                    ItemCategorySpec(R.string.item_cat_lunar_blade, "⚔️"),
                    ItemCategorySpec(R.string.item_cat_shadow_dagger, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_holy_wand, "🌟", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_dark_scepter, "🔮", isMagicalWeapon = true)
                ).random()
                5 -> listOf(
                    ItemCategorySpec(R.string.item_cat_rift_edge, "⚔️"),
                    ItemCategorySpec(R.string.item_cat_brave_sword, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_elemental_rod, "🪄", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_mystic_wand, "🔮", isMagicalWeapon = true)
                ).random()
                6 -> listOf(
                    ItemCategorySpec(R.string.item_cat_runeblade, "⚔️"),
                    ItemCategorySpec(R.string.item_cat_chainsaw, "⚙️"),
                    ItemCategorySpec(R.string.item_cat_magitek_rod, "🪄", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_esper_wand, "🔮", isMagicalWeapon = true)
                ).random()
                7 -> listOf(
                    ItemCategorySpec(R.string.item_cat_buster_sword, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_leather_glove, "🥊"),
                    ItemCategorySpec(R.string.item_cat_gatling_gun, "🔫"),
                    ItemCategorySpec(R.string.item_cat_mako_staff, "🪄", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_materia_wand, "🔮", isMagicalWeapon = true)
                ).random()
                8 -> listOf(
                    ItemCategorySpec(R.string.item_cat_gunblade, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_pinwheel, "🪃"),
                    ItemCategorySpec(R.string.item_cat_guardian_wand, "🪄", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_sorceress_rod, "🔮", isMagicalWeapon = true)
                ).random()
                9 -> listOf(
                    ItemCategorySpec(R.string.item_cat_mage_masher, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_thief_dagger, "🗡️"),
                    ItemCategorySpec(R.string.item_cat_cypress_pole, "🪄", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_summoner_flute, "🪈", isMagicalWeapon = true)
                ).random()
                else -> listOf(
                    ItemCategorySpec(R.string.item_cat_dimensional_blade, "⚔️"),
                    ItemCategorySpec(R.string.item_cat_void_scythe, "🌌"),
                    ItemCategorySpec(R.string.item_cat_astral_wand, "🌟", isMagicalWeapon = true),
                    ItemCategorySpec(R.string.item_cat_celestial_rod, "🔮", isMagicalWeapon = true)
                ).random()
            }
        }

        private fun getArmorCategory(dimension: Int): ItemCategorySpec {
            return when (dimension) {
                1 -> listOf(
                    ItemCategorySpec(R.string.item_cat_plate_armor, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_leather_helm, "🪖"),
                    ItemCategorySpec(R.string.item_cat_cloth_robe, "👘")
                ).random()
                2 -> listOf(
                    ItemCategorySpec(R.string.item_cat_scale_mail, "🦺"),
                    ItemCategorySpec(R.string.item_cat_heavy_cuirass, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_mithril_vest, "🦺")
                ).random()
                3 -> listOf(
                    ItemCategorySpec(R.string.item_cat_dragon_scale_armor, "🐉"),
                    ItemCategorySpec(R.string.item_cat_archmage_robes, "🔮"),
                    ItemCategorySpec(R.string.item_cat_knight_mail, "🛡️")
                ).random()
                4 -> listOf(
                    ItemCategorySpec(R.string.item_cat_paladin_cuirass, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_dark_armor, "🖤"),
                    ItemCategorySpec(R.string.item_cat_white_robe, "🤍")
                ).random()
                5 -> listOf(
                    ItemCategorySpec(R.string.item_cat_mirage_vest, "🦺"),
                    ItemCategorySpec(R.string.item_cat_diamond_armor, "💎"),
                    ItemCategorySpec(R.string.item_cat_summoner_garb, "👘")
                ).random()
                6 -> listOf(
                    ItemCategorySpec(R.string.item_cat_genji_armor, "🥷"),
                    ItemCategorySpec(R.string.item_cat_magitek_coat, "🧥"),
                    ItemCategorySpec(R.string.item_cat_silk_robe, "👘")
                ).random()
                7 -> listOf(
                    ItemCategorySpec(R.string.item_cat_soldier_uniform, "👔"),
                    ItemCategorySpec(R.string.item_cat_shinra_vest, "🦺"),
                    ItemCategorySpec(R.string.item_cat_aegis_coat, "🧥")
                ).random()
                8 -> listOf(
                    ItemCategorySpec(R.string.item_cat_seed_uniform, "👔"),
                    ItemCategorySpec(R.string.item_cat_balamb_jacket, "🧥"),
                    ItemCategorySpec(R.string.item_cat_sorceress_gown, "👗")
                ).random()
                9 -> listOf(
                    ItemCategorySpec(R.string.item_cat_grand_armor, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_black_cowl, "🧢"),
                    ItemCategorySpec(R.string.item_cat_feather_hat, "👒")
                ).random()
                else -> listOf(
                    ItemCategorySpec(R.string.item_cat_dimensional_cuirass, "🌌"),
                    ItemCategorySpec(R.string.item_cat_astral_vestment, "🌟"),
                    ItemCategorySpec(R.string.item_cat_celestial_garb, "✨")
                ).random()
            }
        }

        private fun getShieldCategory(dimension: Int): ItemCategorySpec {
            return when (dimension) {
                1 -> listOf(
                    ItemCategorySpec(R.string.item_cat_round_shield, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_guard_shield, "🔰")
                ).random()
                2 -> listOf(
                    ItemCategorySpec(R.string.item_cat_aegis_shield, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_tower_shield, "🛡️")
                ).random()
                3 -> listOf(
                    ItemCategorySpec(R.string.item_cat_force_shield, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_mirror_shield, "🪞")
                ).random()
                4 -> listOf(
                    ItemCategorySpec(R.string.item_cat_lunar_barrier, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_light_shield, "🛡️")
                ).random()
                5 -> listOf(
                    ItemCategorySpec(R.string.item_cat_elemental_aegis, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_crystal_shield, "💎")
                ).random()
                6 -> listOf(
                    ItemCategorySpec(R.string.item_cat_genji_shield, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_magitek_barrier, "🛡️")
                ).random()
                7 -> listOf(
                    ItemCategorySpec(R.string.item_cat_mythril_armlet, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_guard_bracelet, "🛡️")
                ).random()
                8 -> listOf(
                    ItemCategorySpec(R.string.item_cat_guardian_shield, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_force_aegis, "🛡️")
                ).random()
                9 -> listOf(
                    ItemCategorySpec(R.string.item_cat_shield_alexandria, "🛡️"),
                    ItemCategorySpec(R.string.item_cat_dragon_shield, "🛡️")
                ).random()
                else -> listOf(
                    ItemCategorySpec(R.string.item_cat_dimensional_barrier, "🌀"),
                    ItemCategorySpec(R.string.item_cat_void_aegis, "🛡️")
                ).random()
            }
        }

        private fun getAccessoryCategory(dimension: Int): ItemCategorySpec {
            return when (dimension) {
                1 -> listOf(
                    ItemCategorySpec(R.string.item_cat_copper_ring, "💍"),
                    ItemCategorySpec(R.string.item_cat_silver_pendant, "📿")
                ).random()
                2 -> listOf(
                    ItemCategorySpec(R.string.item_cat_ruby_bracelet, "📿"),
                    ItemCategorySpec(R.string.item_cat_jade_earring, "💎")
                ).random()
                3 -> listOf(
                    ItemCategorySpec(R.string.item_cat_relic_amulet, "🧿"),
                    ItemCategorySpec(R.string.item_cat_cosmic_charm, "🔮")
                ).random()
                4 -> listOf(
                    ItemCategorySpec(R.string.item_cat_moonstone_ring, "🌙"),
                    ItemCategorySpec(R.string.item_cat_star_pendant, "🌟")
                ).random()
                5 -> listOf(
                    ItemCategorySpec(R.string.item_cat_flame_ring, "🔥"),
                    ItemCategorySpec(R.string.item_cat_angel_ring, "👼")
                ).random()
                6 -> listOf(
                    ItemCategorySpec(R.string.item_cat_relic_ring, "💍"),
                    ItemCategorySpec(R.string.item_cat_earring_power, "💎")
                ).random()
                7 -> listOf(
                    ItemCategorySpec(R.string.item_cat_ribbon, "🎀"),
                    ItemCategorySpec(R.string.item_cat_mako_earring, "💎")
                ).random()
                8 -> listOf(
                    ItemCategorySpec(R.string.item_cat_gf_talisman, "📿"),
                    ItemCategorySpec(R.string.item_cat_sorceress_crown, "👑")
                ).random()
                9 -> listOf(
                    ItemCategorySpec(R.string.item_cat_pumice_piece, "🪨"),
                    ItemCategorySpec(R.string.item_cat_garnet_amulet, "🔴")
                ).random()
                else -> listOf(
                    ItemCategorySpec(R.string.item_cat_dimensional_core, "🌌"),
                    ItemCategorySpec(R.string.item_cat_cosmic_crest, "👑")
                ).random()
            }
        }
    }
}
