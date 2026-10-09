# Skill: Crafting, Material Drops & Gear Socketing

## Core Anchors
- Models: `@Material.kt`, `@Item.kt`, `@MaterialInventoryEntity.kt`
- Engine & Repo: `@FFBattleEngine.kt`, `@GameRepository.kt`
- DAOs: `@MaterialDao.kt`, `@ItemDao.kt`
- ViewModels & UI: `@CraftingViewModel.kt`, `@EquipmentViewModel.kt`, `@CraftingScreen.kt`

## Data Flow & Cross-Feature Coupling
- Loot Generation: Slain enemies/bosses in `FFBattleEngine` evaluate material drop tables based on `floor` and `MonsterType`, emitting `MaterialDrop` events directly to `GameRepository.addMaterials()`.
- Gear Enhancement: Upgrading gear (`+1` to `+10`) consumes Gold + Materials from `MaterialDao`, recalculating item stats and `PowerScore` without altering base item identity.
- Gem Socketing & Fusion: Sockets are added to items using Ores + Gold. Inserting/Fusing crafted Gems updates item `sockets` and recalculates effective hero stats in `FFBattleEngine`.
- Hero Breakthrough: Reaching max Job Level queries `MaterialDao` for required Boss Trophies to unlock higher level caps.

## Key Signatures & Execution Steps
1. `enhanceEquipment(item: Item): Result<Item>`: Deducts required ores/gold, increments `enhancementLevel`, scales stats (`base * (1 + level * 0.05)`), and saves item.
2. `addSocketSlot(item: Item): Result<Item>`: Verifies slot limits (Max 3 weapons/armor, Max 1 accessory), deducts materials, appends `SocketSlot`.
3. `craftGem(recipeId: String): Result<Material>`: Deducts required essences/parts, grants target gem material to inventory.
4. `fuseGems(gemId: String, amount: Int = 3): Result<Material>`: Consumes 3 lower-tier gems to produce 1 higher-tier gem.
5. `uncapHeroLevel(heroId: String): Result<Unit>`: Validates max job level and required boss trophies, increments `maxLevelCap`.

## Feature Invariants
- Item Integrity: Enhancing or socketing gear MUST preserve original item IDs, equipped state, and `ownerId`.
- Socket Capacity Rules: Weapons and Armor cannot exceed 3 sockets. Accessories cannot exceed 1 socket.
- Material Non-Negativity: Material inventory quantities can never drop below 0. Transactions must validate sufficient material counts prior to state mutation.
- Enhancement Cap: Equipment cannot exceed `+10` enhancement level.