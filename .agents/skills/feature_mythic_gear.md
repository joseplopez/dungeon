# Skill: Mythic Gear & Set Bonuses

## Core Anchors
- Models: `MythicSet.kt`, `Item.kt` (Rarity.MYTHIC)
- Engine: `Hero.kt` (Stat Calculation), `FFBattleEngine.kt` / `MonsterLootTable.kt` (Drop Logic)
- UI: `EquipmentScreen.kt`, `CraftingScreen.kt`

## Data Flow & Feature Coupling
- **Set Bonus Activation:** A Set Bonus is ONLY active if the hero has equipped `requiredPieces` (default: 3) items sharing the same `setId`, AND the hero's `HeroClass` exactly matches the `MythicSet.targetClass`.
- **Infinite Scaling Math:** Because standard gear stats scale infinitely, Mythic Set bonuses MUST be applied as **percentage multipliers** (e.g., `* 1.40f`) on the FINAL calculated stat total, never as flat additions. 
- **Database Interop:** Adding `setId` to `Item` requires strict adherence to `SKILL_DATABASE_MANAGEMENT.md` (Version bump + explicit `Migration` object with try/catch emergency reset).

## Drop Engine Invariants (The "Hybrid" Rule)
Mythic drops are strictly gated to preserve early-game balance and emphasize endgame crafting:
- **Floors 1 – 99:** 0% drop rate for any Mythic entity.
- **Floors 1000+ Bosses:** 25% chance to drop 1 "Mythic Shard" (Crafting Material).
- **Floors 300+ Bosses:** 0.25% (1 in 400) chance to drop a direct Mythic Gear piece.
- **Floors 300+ Standard Enemies:** 0.01% (1 in 10,000) chance to drop a direct Mythic Gear piece.

## The Mythic Forge (Crafting Cost)
Crafting a piece of Mythic Gear is a monumental endgame grind. The exact recipe is:
1. **1x Level 10 Legendary Item** (Must match the target gear slot being crafted).
2. **50x Mythic Shards** (Requires defeating X bosses past Floor 1000).
3. **5x Boss Trophy 3** (Endgame boss materials).

## Defined Sets (Lore & Synergy)
1.  **CRYSTAL (Knight):** +40% Total DEF, +25% Total HP (Ultimate Physical Wall)
2.  **MAGUS (Black Mage):** +40% Total MAG, +15% Flat Crit Chance (Glass Cannon Magic)
3.  **DEVOUT (White Mage):** +30% Total MAG, +40% Total MP (Infinite Healing Sustain)
4.  **SHADOW (Thief/Ninja):** +25% Total ATK, +20% Flat Crit Chance (Lethal Fast Strikes)
5.  **CHAKRA (Monk):** +35% Total ATK, +50% Flat Crit Damage (Heavy Burst Melee)
6.  **WYVERN (Dragoon):** +30% Total ATK, +15% Total DEF, +30% Flat Crit Damage (Bruiser)
7.  **CRIMSON (Red Mage):** +25% Total ATK, +25% Total MAG, +20% Total MP (Jack of All Trades)
8.  **EVOKER (Summoner):** +45% Total MAG, -10% Total HP (Extreme AoE Nuke, Fragile)
9.  **APOLLO (Bard):** +30% Total HP, +50% Total MP (Unkillable Support)
10. **GENJI (Samurai):** +40% Total ATK, +30% Flat Crit Damage (Sword Master)
11. **AEGIS (Paladin):** +35% Total HP, +35% Total DEF (Ultimate Hybrid Wall)
12. **ONION (Onion Knight):** +30% ATK, +30% DEF, +30% MAG, +30% HP (The Secret God Tier)