# Skill: Global Project Architecture & Systems Master Overview

## 📌 Document Purpose
This document is the **authoritative master overview** of the entire *Final Dungeon* repository. It synthesizes all core mechanics, architectural layers, data flows, persistence rules, combat math, visual standards, and non-negotiable invariants into a single reference for AI agents.

---

## 🏛️ High-Level Game Architecture & Progression Loop

*Final Dungeon* is an idle dungeon crawler RPG built with **Jetpack Compose**, **Room (v33)**, **Hilt**, and **Kotlin Coroutines/Flow**. It fuses *Soda Dungeon 2*'s tavern/inn hiring, gold-rush economy, and dimension reset loops with *Final Fantasy*'s iconic 16-bit JRPG job system, elemental crystals, and atmospheric biomes.

### The Core Loop
```
                ┌──────────────────────────────────────┐
                │        Grand Capital (Town)          │
                │  - Facility Upgrades (Armory, Forge) │
                │  - Crystal Shop (Unlock Jobs)        │
                │  - Hidden Boss Lair Entrance (Dim 11)│
                └──────────────────┬───────────────────┘
                                   │
                                   ▼
                ┌──────────────────────────────────────┐
                │           The Astral Inn             │
                │  - Recruit / Dismiss Warriors        │
                │  - Party Formation (3 to 5 slots)    │
                │  - Resting & Pet Selection           │
                │  - Dimension Advance (Prestige)      │
                └──────────────────┬───────────────────┘
                                   │
                                   ▼
                ┌──────────────────────────────────────┐
                │            Dungeon Crawl             │
                │  - FFBattleEngine ATB Combat Loop    │
                │  - Biomes (23 Canvas Backgrounds)    │
                │  - Boss Fights, Materials & Relics   │
                └──────────────┬──────────────┬────────┘
                               │              │
           Victory / Retreat   │              │ Wipeout
                               ▼              ▼
                ┌──────────────────────┐   ┌──────────────────────┐
                │ Crafting & Equipment │   │ Permadeath / Revive  │
                │ - Enhance (+1 to +10)│   │ - Penalty or Ad/IAP  │
                │ - Sockets & Gems     │   │ - All Gear Returned  │
                │ - Quick Equip        │   │   to Inventory       │
                └──────────────────────┘   └──────────────────────┘
```

---

## 🛡️ Critical Invariants (CRITICAL - DO NOT BREAK)

1. **Item Recovery Invariant**:
   Hero removal, dismissal, or party death MUST return all equipped gear/items to inventory via [`GameRepository.removeHero()`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/repository/GameRepository.kt#L256-L260) and [`database.itemDao.unequipAllFromHero()`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/db/ItemDao.kt). Items must **never** be lost or deleted on defeat.
2. **Stat Mutation Invariant**:
   Combat stats in [`FFBattleEngine`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/engine/FFBattleEngine.kt) and [`Hero.calculateStats()`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/Hero.kt#L248-L305) MUST be calculated strictly as:
   $$\text{Final Stats} = \text{Base Stats (Hero)} \times \text{Relic Multipliers (RelicType)} + \text{Gear Stats (Item)}$$
3. **Room Migration Rule**:
   All database updates require strict schema preservation with explicit migration scripts in [`GameDatabase.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/db/GameDatabase.kt) (current version is **v33**). Whenever an `@Entity` is modified:
   - Increment version by exactly 1.
   - Create explicit `MIGRATION_X_Y` using `ensureColumn()` checks wrapped in `try-catch`.
   - Never use empty/no-op migrations when removing/ignoring columns; use the Table Recreate pattern.
   - `fallbackToDestructiveMigration` is strictly a secondary fail-safe.
4. **Guaranteed Boss Loot Rule**:
   Defeating dungeon bosses MUST always drop [`Rarity.RARE`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/Item.kt#L20-L26) or higher quality items, influenced by Magicite Magnet and Double Loot relics.
5. **Localization Protocol**:
   Zero hardcoded user-facing strings in UI/Compose. Every string must be defined in [`app/src/main/res/values/strings.xml`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/res/values/strings.xml) using standard placeholders (`%1$s`, `%1$d`) without `<xliff:g>` tags in English.
6. **Pixel-Art Standards**:
   - **Heroes/Monsters**: Defined 100% pixel-by-pixel using uniform 2D string matrices in [`PixelCharacters.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/ui/components/PixelCharacters.kt) or batch-converted using [`convert.py`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/convert.py). Heroes face **RIGHT**; monsters face **LEFT**.
   - **Biome Backgrounds**: Rendered in [`PixelBackground.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/ui/components/PixelBackground.kt) following the mandatory 7-layer `DrawScope` architecture.

---

## 🧩 Subsystem Architecture & Mechanics

### 1. Hero & Job Class System
Located in [`data/models/Hero.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/Hero.kt), [`HeroClass.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/HeroClass.kt), and [`JobAbility.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/JobAbility.kt).

- **18 Total Jobs** divided into 3 Tiers:
  - **Tier 1 (Basic)**: Freelancer (Free, Clear Crystal), Warrior, White Mage, Black Mage, Thief, Monk.
  - **Tier 2 (Advanced)**: Knight, Paladin, Red Mage, Summoner, Ninja, Dragoon, Bard, Samurai. (Requires Inn Level >= 1 & Crystal purchase).
  - **Tier 3 (Hidden Secrets)**: Onion Knight, Mime, Necromancer, Blue Mage. (Requires discovery condition in GameState).
- **Stat Growth & Level Caps**:
  - Heroes start at Level 1 with default cap of Level 20.
  - Reaching caps requires **Breakthrough** in Crafting using Boss Trophies (`boss_trophy_1` for Lvl 40, `boss_trophy_2` for Lvl 60, `boss_trophy_3` beyond).
  - Onion Knight has unique late-game scaling: weak before Level 30, quadratic powerhouse after Level 30 ($500 + (\text{level} - 30)^2 \times 250$).
- **Job Abilities & Charge Meter**:
  - Ability meter charges 0 to 3 in combat; at 3, executes signature active skill.
  - Each job has 3 unlockable ability tiers (Level 1, Level 10, Level 25).
- **Global Job Mastery**:
  - Job EXP is accumulated globally per class across all runs.
  - Each level grants permanent passive stat bonuses to that job class (`StatType`).

### 2. Equipment, Loot & Crafting System
Located in [`data/models/Item.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/Item.kt), [`Material.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/Material.kt), [`MythicSet.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/MythicSet.kt), and [`CraftingViewModel.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/ui/viewmodels/CraftingViewModel.kt).

- **Equipment Slots**: `WEAPON`, `ARMOR`, `SHIELD`, `ACCESSORY` (heroes equip up to 2 accessories).
- **Rarities**: `COMMON` (Grey), `RARE` (Blue), `EPIC` (Purple), `LEGENDARY` (Gold), `MYTHIC` (Red/Pink).
- **PowerScore**:
  $$\text{PowerScore} = \text{BaseScore} \times (1.0 + \text{EnhancementLevel} \times 0.05) + \text{SocketBonus}$$
  Used by `Quick Equip` to auto-equip the best items for heroes.
- **Crafting Features (5 Tabs)**:
  1. **Enhance**: Upgrade gear +1 to +10 using Ores (`iron_ore`, `mithril_ore`, `adamantite_ore`, `orichalcum_ore`) and Gil.
  2. **Socket**: Add socket slots (max 3 for weapons/armors, max 1 for accessories) using Ores and Gil.
  3. **Gem Fusion**: Socket gems (Ruby: ATK, Sapphire: MAG, Emerald: DEF, Topaz: HP) or fuse 3 lower-tier gems into a higher tier.
  4. **Breakthrough**: Consume Boss Trophies to unlock hero max level caps.
  5. **Mythic Forge**: Forge class-specific 3-piece Mythic Sets using Mythic Shards and Boss Trophies.
- **Item Locking**: Items can be locked (`isLocked = true`) to prevent accidental selling via "Sell All".

### 3. Combat Engine & Game Modes
Located in [`engine/FFBattleEngine.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/engine/FFBattleEngine.kt) and [`ui/viewmodels/DungeonViewModel.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/ui/viewmodels/DungeonViewModel.kt).

- **ATB Speed-Based Turn System**: Turn order sorted dynamically by unit `speed`.
- **Hero AI Priorities**:
  - `ATTACK`: Prioritize high-threat targets with physical attacks.
  - `MAGIC`: Cast spells, exploit weaknesses, manage MP.
  - `HEAL`: Prioritize allies below critical HP thresholds.
  - `DEFEND`: Reduce incoming damage and guard allies.
- **Event-Driven Architecture**: Emits reactive [`FFBattleEvent`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/engine/FFBattleEngine.kt#L12-L30) streams (`DamageDealt`, `HealCast`, `AbilityUsed`, `FloorComplete`, `EnemyDefeated`, `BossDefeated`, `AllHeroesFell`).
- **Dungeon Modes**:
  - **Regular Dungeon**: 100 floors per dimension. Floor ramp onboardings, biome transitions, dimension boss checkpoints every 20 floors.
  - **Boss Lair (Endgame, Dimension 11+)**: Endless boss gauntlet. Flat x100 stat spike, +10% compounding difficulty per floor, zero entry cost, 25% Mythic Shard drop chance, 5% Mythic Gear drop chance.
- **Death & Defeat Penalties**:
  - Party wipeout incurs a 30% Gil penalty on earnings for that run unless Permadeath Protection is active or an ad-rewarded revive is used.
  - Fallen heroes without protection are permanently removed, but all gear is returned to inventory.

### 4. Progression & Dimensions (Prestige System)
Located in [`data/models/FFDimensionData.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/FFDimensionData.kt) and [`ui/viewmodels/InnViewModel.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/ui/viewmodels/InnViewModel.kt).

- **10 Story Dimensions + Infinite Void**:
  - Dim 1: Final Fantasy I (Warriors of Light, 100 floors, Chaos)
  - Dim 2: Final Fantasy II (Rebellion of Souls, 200 floors, Emperor)
  - Dim 3: Final Fantasy III (Cloud of Darkness, 300 floors)
  - Dim 4: Final Fantasy IV (Red Wings & Lunarians, 400 floors, Zeromus)
  - Dim 5: Final Fantasy V (Void & Four Crystals, 500 floors, Neo Exdeath)
  - Dim 6: Final Fantasy VI (The Ruined World, 600 floors, Kefka)
  - Dim 7: Final Fantasy VII (Mako & Lifestream, 700 floors, Sephiroth)
  - Dim 8: Final Fantasy VIII (Time Compression, 800 floors, Ultimecia)
  - Dim 9: Final Fantasy IX (Crystal World, 900 floors, Necron)
  - Dim 10: Final Fantasy X (The Eternal Calm, 1000 floors, Yu Yevon)
  - Dim 11+: Endless Shattered Void & Boss Lair.
- **Dimension Advance ("Ascension")**:
  - Unlocked upon beating the dimension's final boss.
  - Resets roster and inventory (returning value).
  - Awards massive Magicite lump sum ($250 \times \text{dim}$) plus scaling percentage ($50\% + 10\% \times (\text{dim} - 1)$).
  - Retains a percentage of Gil via the **Deep Pockets** relic.
  - Hall of Fame tracks clear time, Gil earned, bosses slain, and items discovered.

### 5. Town Facilities & Relics
Located in [`ui/viewmodels/TownViewModel.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/ui/viewmodels/TownViewModel.kt) and [`RelicsViewModel.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/ui/viewmodels/RelicsViewModel.kt).

- **13 Town Upgrades**:
  - `INN`: Unlocks Tier 2 advanced jobs (Max 1).
  - `BARRACKS`: Expands party size from 3 to 4 to 5 (Max 2).
  - `VAULT`: Increases gold storage cap up to unlimited (Max 10).
  - `ARMORY`: Increases base stats of dropped items (Max 20).
  - `MAGIC_SHOP`: Boosts drop odds for rare/epic/legendary items (Max 10).
  - `PATHFINDER`: Allows selecting starting floor (25%, 50%, 75%, 100% of max) (Max 4).
  - `TRAINING`: +10% EXP gained per level (Max 10).
  - `PLANNING`: -2% upgrade discount per level (Max 20).
  - `CLINIC`: -10% inn rest cost per level (Max 10).
  - `ALCHEMIST`: +10% Magicite drop & yield (Max 10).
  - `LIBRARY`: +20% Job Mastery EXP gain (Max 10).
  - `FORGE`: +5% Mythic drop rate & +10% Mythic stats (Max 10).
  - `WAR_ROOM`: +15% Crit DMG & +5% Ability Charge rate (Max 10).
- **Relics (Permanent Magicite Upgrades)**:
  - Tier 1: Attack, HP, MP, Magic, Defense, Crit Chance, Crit Damage, Gold %, Magicite Find %.
  - Ascended (Tier 2): Magicite Magnet (+5% drop rate), Deep Pockets (+10% gold kept on dimension advance), Double Loot (+5% boss double drop chance).
  - Gold-to-Magicite Exchange: Allows converting excess gold into Magicite.

### 6. Codex, Bestiary & Milestones
Located in [`data/models/CodexEntry.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/CodexEntry.kt) and [`ui/viewmodels/CodexViewModel.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/ui/viewmodels/CodexViewModel.kt).

- Tracks discovered monsters, kill counts, drop tables, and discovered materials.
- Kill Thresholds: Unseen, Sighted (1 kill), Slain (10 kills), Mastered (50 kills).
- Milestone Completion Bonuses: Grant global passive stat buffs (+ATK, +Gold, +Magicite Chance, +Crit) at 10%, 25%, 50%, 75%, and 100% completion.

### 7. Pets System
Located in [`data/models/Pet.kt`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/models/Pet.kt).

- Permanent companion companions unlocked with Gil:
  - **Chocobo**: +20% Item Find rate (10,000 Gil)
  - **Cat**: +30% Gil Find rate (25,000 Gil)
  - **Moogle**: +50% EXP Boost (50,000 Gil)
  - **Cactuar**: +15% Crit Chance (100,000 Gil)
  - **Tonberry**: +100% Crit Damage (250,000 Gil)

### 8. Monetization, Ads & Cloud Sync
Located in [`monetization/`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/monetization) and [`data/repository/`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/app/src/main/java/com/game/dungeon/data/repository).

- **Google Mobile Ads (AdMob)**:
  - Boost Ad: Skip floors with starting boosts.
  - Revive Ad: Free full party revive in battle once per run.
  - Retreat Revive Ad: Recover fallen heroes before retreating.
  - Town/Inn Ads: Free scaling Gil and Magicite grants.
- **Google Play In-App Billing**:
  - Gil and Magicite resource packs.
  - Premium Hyper Battle Speed.
  - Permanent Permadeath Protection unlock.
- **Firebase Auth & Cloud Leaderboards**:
  - Anonymous / Google Auth login.
  - Real-time score and floor uploads to Firebase Firestore leaderboards.

---

## 🗺️ Specialized Skill Files Quick Reference

When working on specific subsystems, refer directly to these contract skill files in [`.agents/skills/`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills):

| Domain / Subsystem | Skill File | Core Responsibility |
| :--- | :--- | :--- |
| **Database & Room** | [`SKILL_DATABASE_MANAGEMENT.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/SKILL_DATABASE_MANAGEMENT.md) | Room v33 migrations, table recreate patterns, safety nets. |
| **Localization** | [`SKILL_LOCALIZATION_PROTOCOL.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/SKILL_LOCALIZATION_PROTOCOL.md) | `strings.xml` management across 17 languages, zero hardcoding. |
| **Combat Loop** | [`feature_combat.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_combat.md) | `FFBattleEngine`, turn order, AI priorities, loot drops. |
| **Boss Lair** | [`boss-lair-feature.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/boss-lair-feature.md) | Dimension 11+ endless boss dungeon, x100 scaling, mythic loot. |
| **Crafting & Sockets** | [`feature_crafting_socketing.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_crafting_socketing.md) | Enhancement (+10), socketing, gem fusion, breakthrough caps. |
| **Mythic Sets** | [`feature_mythic_gear.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_mythic_gear.md) | 12 class-specific 3-piece sets, allowed slots, stat multipliers. |
| **Equipment & Inventory**| [`feature_equipment.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_equipment.md) | Quick equip algorithm, power score, gear locking, selling. |
| **Prestige & Dimensions**| [`feature_inn_prestige.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_inn_prestige.md) | Dimension advance, hall of fame, gold/magicite rewards. |
| **Relics & Upgrades** | [`feature_relics.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_relics.md) | Permanent Magicite upgrades, ascended perks, gold exchange. |
| **Job Mastery** | [`feature_mastery.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_mastery.md) | Global job EXP, level 1-50 perks, ability unlocks (Lvl 1/10/25). |
| **Codex & Bestiary** | [`feature_codex.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_codex.md) | Monster & material discovery, kill thresholds, milestone buffs. |
| **Pets & Companions** | [`feature_pets.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_pets.md) | Chocobo, Moogle, Cat, Cactuar, Tonberry unlock and stat boosts. |
| **Leaderboards** | [`feature_leaderboard.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_leaderboard.md) | Firebase score uploads, dimension rankings, Boss Lair records. |
| **Monetization** | [`feature_monetization.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/feature_monetization.md) | AdMob rewarded ads, Play Billing products, speed/protection. |
| **Biome Backgrounds** | [`SKILL_BIOME_BACKGROUNDS.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/SKILL_BIOME_BACKGROUNDS.md) | Mandatory 7-layer `DrawScope` canvas architecture for biomes. |
| **Hero/Monster Sprites**| [`SKILL_HERO_SPRITES.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/SKILL_HERO_SPRITES.md) | Pixel-by-pixel 2D string matrices, palettes, orientation rules. |
| **Sprite Converter** | [`SKILL_SPRITE_CONVERSION.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/SKILL_SPRITE_CONVERSION.md) | Python `convert.py` tool for automated sprite matrix transcription. |
| **Token Optimization** | [`meta_feature_workflow.md`](file:///Users/jple/AndroidStudioProjects/Finaldungeon3/.agents/skills/meta_feature_workflow.md) | Token-efficient feature workflow, zero-discovery scans. |
