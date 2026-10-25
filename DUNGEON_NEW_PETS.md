
**Context:** We are implementing an end-game "Safari Dimension" with Tier 2 Companion Monsters and a dedicated "Menagerie" UI, alongside a new contextual IAP for utility items.
**Files to reference:** `GameState.kt`, `Pet.kt`, `InAppProduct.kt`, `GameRepository.kt`, `InnScreen.kt`, `InnViewModel.kt` (or your Menagerie ViewModel), `FFBattleEngine.kt`, `FFDimensionData.kt`, and `DungeonViewModel.kt`.

Please execute this implementation step-by-step exactly as outlined below. Await my command for each phase.

---

### Phase 1: Safari State, Models & Unlock Logic

**Goal:** Update state to track Safari elements, Tier 2 pets, and enforce unlock conditions.

1. **New Data Class:** Create `Tier2Pet(val petId: String, val rarity: Rarity = Rarity.COMMON, val cardsCollected: Int = 0, val currentRank: Int = 0)` where Rank 0 is locked and Rank 5 is max. Add `fun getStatMultiplier(): Float = 1f + (currentRank * 0.01f)` (1% to 5% boost).
2. **State Additions:** In `GameState.kt`, add:


* `val hasSeenSafariUnlockPopup: Boolean = false`
* `val safariTickets: Int = 0`
* `val tier2Pets: Map<String, Tier2Pet> = emptyMap()`
* `val equippedTier2PetId: String? = null`


3. **Unlock Helpers:** Add `fun isSafariUnlocked() = currentDimension >= 11 && lifetimeHighestFloor >= 1000`. In pet leveling logic, cap all Tier 1 pets at level 50.



---

### Phase 2: Contextual Ticket Acquisition (IAP, Ads, & Drops)

**Goal:** Implement the ways players get Safari Tickets directly in the Menagerie.

1. **Combat Drops:** In `FFBattleEngine.kt`, defeating a normal Boss has a 10% chance to drop a `Safari Ticket`.


2. **IAP Integration:** In `InAppProduct.kt`, ensure `MISC_PACK` exists. In the ViewModel managing the Menagerie, add a billing flow. When a user buys the Miscellaneous Pack from the Menagerie screen, grant them **10 Safari Tickets**.
3. **Ad Integration:** In the ViewModel managing the Menagerie, add a method to watch a rewarded ad to receive **1 Safari Ticket**.

---

### Phase 3: The Safari Sandbox & Combat Drops

**Goal:** Enforce a "Base Stats Only" mode, implement card drops, and pet combat turns.

1. **Sandbox Restriction:** In `DungeonViewModel.kt` (`startRun`), if `mode == DungeonMode.SAFARI`, strip the party of all gear, relic, and mastery bonuses. They must enter with strictly base stats multiplied ONLY by their `Tier2Pet.getStatMultiplier()`. Set `defenseBonus = 0` (no armor).
2. **Loot Drops (`FFBattleEngine.kt`):** In Safari runs, enemies have a 30% chance to drop their specific `MonsterCard` (Dispatch `FFBattleEvent.MonsterCardDropped`).
3. **Rank 5 Active Skill:** In `FFBattleEngine.kt` turn calculation, if `equippedTier2Pet` is Rank 5, insert a pet turn (e.g., 120 Speed). When the pet acts, emit `FFBattleEvent.AbilityUsed` (e.g., Fairy casts Heal, Direwolf attacks).



---

### Phase 4: Safari Enemy Spawning & Difficulty

**Goal:** Create the custom Safari Dimension enemies with balanced math and correct visuals.

1. **Custom Dimension (`FFDimensionData.kt`):** Define the Safari Dimension. The enemies in this dimension must correspond to the available pets.
2. **Sprite Mapping:** Ensure that when rendering an enemy in `DungeonMode.SAFARI`, the UI uses the `PetType` sprites instead of standard monster sprites, so players know which card they are hunting.


3. **Difficulty Scaling (`FFBattleEngine.kt`):** Because heroes have NO gear, NO relics, and NO masteries in Safari mode, the enemies must have drastically reduced HP and ATK. Bypass the standard exponential floor scaling. Create a custom linear or soft-capped stat curve for Safari enemies so they are balanced strictly around level 99 base hero stats.



---

### Phase 5: Town Integration & Menagerie UI

**Goal:** Move pets to a dedicated screen and build a grid-based card fusion UI.

1. **Town Update (`InnScreen.kt`):**
* If `isSafariUnlocked()` is true, show a new "Menagerie" button on the guild stage.


* If unlocked but `hasSeenSafariUnlockPopup` is false, show a popup explaining Safari mode, then mark it seen.


* Remove the old Pet tab from the Masteries screen.


2. **New Screen (`MenagerieScreen.kt`):**
* **Top Section:** Show currently equipped Tier 1 and Tier 2 pets, plus the player's current Safari Ticket count. Add contextual buttons here to "Watch Ad (+1 Ticket)" and "Buy Misc Pack (+10 Tickets)".
* **Tabs:** "Tier 1 (Passives)" and "Tier 2 (Cards)".
* **Tier 2 Grid:** A 3-column `LazyVerticalGrid`.
* **Card Design:** Background color matches `rarity`. Top-Left hexagon for `currentRank`. Top-Right pill for fractional progress (`cardsCollected / needed`). Center sprite. Bottom name banner.
* **Locks:** If Tier 1 equivalent is NOT level 50, overlay the card with a dark tint and "Requires Level 50 Tier 1". If Safari is locked, overlay the entire Tier 2 tab with the Dimension 11 / Floor 1000 requirement.