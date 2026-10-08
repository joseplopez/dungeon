# Skill: Equipment & Inventory System

## Core Anchors
- ViewModel: `@EquipmentViewModel.kt`
- Models & Repo: `@Item.kt`, `@Hero.kt`, `@GameRepository.kt`
- DAO: `@ItemDao.kt`
- UI: `@EquipmentScreen.kt`

## Data Flow & Feature Coupling
- State Flow: `EquipmentViewModel` reads reactive flows `repository.getInventory()` (`ownerId == null`) and `repository.getEquippedItems(heroId)` (`ownerId == heroId`).
- Item Recovery Contract: `GameRepository.removeHero()` executes `database.itemDao.unequipAllFromHero(heroId)` before deleting hero. Ensures equipped gear returns to inventory upon dismissal, death, or party wipe.
- Persistence: Equipping sets `ownerId = heroId`; unequipping sets `ownerId = null`. Handled via `repository.saveItem()`.

## Key Signatures & Execution Steps
1. `equipItem(item: Item)`: Replaces slot gear (max 1 Weapon/Armor/Shield, max 2 Accessories) by resetting old `ownerId` to `null` and setting target `ownerId`.
2. `unequipItem(item: Item)`: Sets `item.ownerId = null` via `saveItem()`.
3. `quickEquip()`: Merges inventory and equipped pool. Evaluates highest `powerScore` (`attack + defense + magic + hp/5 + mp/2 + critChance*2 + critDamage/2`) for each slot, calls `repository.unequipAll(heroId)`, and equips best items.
4. `sellItem(item: Item)` / `sellAllUnequipped()`: Converts unequipped items (`ownerId == null`) to gold via `repository.sellItem()`.

## Feature Invariants
- Item Recovery Invariant: Hero removal/death MUST unequip all gear (`ownerId = null`) to prevent item loss.
- Inventory State: `ownerId == null` represents inventory; non-null `ownerId` represents active hero assignment.
- Unique Slot Limits: Max 1 Weapon, 1 Armor, 1 Shield, and 2 Accessories equipped per hero.
