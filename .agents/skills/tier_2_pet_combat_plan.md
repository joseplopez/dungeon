# Tier 2 Pet Combat Integration Plan (Rank 2 Edition)

This document outlines the step-by-step implementation for making Tier 2 Pets active combatants in the battle system. Feed these phases sequentially to the AI agent to ensure safe and complete integration.

## Phase 1: Engine Logic & Scaling (`FFBattleEngine.kt`)
**Goal:** Allow pets rank 2 and above to participate in the battle loop with dynamic damage/healing scaling based on the party's stats.

1.  **Lower Rank Requirement:**
    *   Inside the `runBattle` function, locate the loop `while (enemies.any { it.currentHp > 0 } && aliveHeroes.any { it.isAlive })`.
    *   Change the pet turn condition: `val hasPetTurn = equippedTier2Pet != null && equippedTier2Pet.currentRank >= 2`.
2.  **Dynamic Combat Formulas:**
    *   Locate the `executePetTurn` function.
    *   Calculate the power multiplier based on rank: `val powerMultiplier = pet.currentRank * 0.05f` (Rank 2 = 10%, Rank 5 = 25%).
    *   **For Healers (Moogle/Fairy):** 
        *   Calculate `partyMaxHp = aliveAllies.sumOf { it.maxHp }`.
        *   Change `healAmount` to `(partyMaxHp * powerMultiplier).toInt().coerceAtLeast(100)`.
    *   **For Attackers:**
        *   Calculate `partyTotalPower = aliveAllies.sumOf { it.attack + it.magic }`.
        *   Change `damage` to `(partyTotalPower * powerMultiplier).toLong().coerceAtLeast(1L)`.

## Phase 2: State Management (`DungeonViewModel.kt`)
**Goal:** Expose the active pet to the UI layer so it can be rendered alongside the heroes.

1.  **Update `FFBattleState`:**
    *   Add a new property to the data class: `val activePet: Tier2Pet? = null`
2.  **Populate the Pet State:**
    *   In the `startRun` function, locate where `battleState.value` is initialized. Pass `activePet = equippedPet` into the `FFBattleState` constructor.
    *   In the `retryRun` function, locate the `battleState.update` call and ensure `activePet = equippedPet` is included if necessary (or rely on the fact it persists, but explicitly setting it is safer).
3.  **Animation Verification:**
    *   Verify that `findIsHero` handles `"pet_"` IDs (it already does, returning true). This ensures damage numbers and attack animations treat the pet as an allied attacker.

## Phase 3: Battle UI Integration (`DungeonScreen.kt`)
**Goal:** Render the pet visually in the combat arena below the heroes and animate its attacks.

1.  **Update `BattleArea` & `HeroesSide`:**
    *   Pass `activePet = state.activePet` down to `HeroesSide`.
    *   Inside `HeroesSide`, change the root `Row` to a `Column`. 
    *   Keep the existing `Row` of heroes at the top of this `Column`.
    *   Add a new `Row` below it, centered, to render the `activePet`.
2.  **Create `PetCombatUnitDisplay` Composable:**
    *   Create a new composable specifically for the pet in combat (similar to `HeroUnitDisplay`).
    *   It should take `petId`, `isAttacking`, and `isHit` (though pets are invincible, we pass false).
    *   Include the `lungeOffset` `Animatable` logic triggered by `isAttacking`.
    *   Render the `PetSprite(petType, ...)` using the matching `PetType.entries.find { it.name == pet.petId }`.
    *   *Do not* include HP or EXP bars. Just the sprite and optionally the pet's name below it.

## Phase 4: Pet UI Communication (`MenagerieScreen.kt`)
**Goal:** Clearly communicate to the player that pets unlock combat at Rank 2 and show their current power.

1.  **Update `Tier2CardItem`:**
    *   Locate the bottom info panel (where the dual-stats text is rendered).
    *   Below the `boostText` `Text` composable, add a new `Text` for combat status.
    *   **Condition 1 (`tier2Pet.currentRank < 2`):** Display "⚔️ Combat Unlock: Rank 2" in `StoneGray`.
    *   **Condition 2 (`tier2Pet.currentRank >= 2`):** Display "⚔️ Combat Active: ${tier2Pet.currentRank * 5}% Power" in `HpGreen` or `GoldBright`.
    *   This gives players a clear incentive to upgrade their Tier 2 pets past Rank 1.