# Feature Blueprint: The Boss Lair (Endgame Dimension 11+)

## 1. Feature Overview
The **Boss Lair** is an endless, boss-only endgame dungeon that becomes accessible once a player reaches Dimension 11. It features a massive difficulty spike, zero entry costs, and highly lucrative Mythic material drop rates.

## 2. Trigger & UI Access (Town Screen)
*   **The Hidden Entrance:** There is no standard UI button for this mode. The entrance is a "spooky, gloomy cave" hidden in the parallax background layers of the `TownScreen`.
*   **Visibility:** The cave is only drawn and clickable if `gameState.currentDimension >= 11`.
*   **Discovery Popup (One-Time Event):**
    *   When the player loads the `TownScreen`, the game checks `currentDimension >= 11` AND `!hasSeenBossLairPopup`.
    *   If true, trigger a "New Area Discovered" dialogue explaining the hidden cave.
    *   Immediately set `hasSeenBossLairPopup = true` in the database to ensure backward compatibility for players already at Dimension 11.

## 3. Preparation Phase
*   **BossLairPrepScreen:** Clicking the cave opens a specialized preparation dialogue/screen.
*   **Checkpointing:** Functions exactly like the Pathfinder tool in the regular dungeon. It tracks and allows the player to start at their `highestBossLairFloor`.
*   **Entry Requirements:** 
    *   Players MUST assign a valid team of heroes to enter.
    *   **Cost:** 0 Stamina / 0 Energy. Entry is completely free and unlimited.

## 4. Battle Engine Rules (`FFBattleEngine.kt`)
The combat engine must accept a new game mode flag (e.g., `mode = DungeonMode.BOSS_LAIR`) that applies the following overrides to standard combat:

### A. Spawning Override
*   **Bosses Only:** Completely bypass standard trash mob generation. 
*   **Roster:** Reuse the existing Dimension Boss templates.
*   **Endless Climb:** The mode has no floor cap. Bosses continue to scale infinitely floor after floor.

### B. Difficulty Multiplier
*   **Baseline x100 Spike:** Calculate the standard stats for the current floor using the regular algorithm, and then apply a flat `x100` multiplier to all generated Boss stats (HP, Attack, Defense, Magic, M.Def, Gil yield, and Exp yield) if `mode == BOSS_LAIR`.

### C. Custom Loot Math (Boss Lair Exclusives)
Bypass standard loot tables for `BOSS_LAIR` mode and apply these fixed rules upon a boss defeat:
1.  **Mythic Shards:** 
    *   25% absolute drop chance per boss kill.
    *   If successful, yield multiple shards (e.g., `Random.nextInt(2, 6)`).
2.  **Mythic Gear:** 
    *   Exactly a 5% drop chance per boss kill.
    *   If successful, drop exactly **one** Mythic item.
    *   *Constraint Check:* The gear drop must use `Item.random(minRarity = Rarity.MYTHIC)` and rigorously apply the `MythicSet.allowedSlots` check so that the generated set perfectly matches the random slot rolled, preventing fallback Canvas sprites.

## 5. Required File Modifications
1.  **`GameState.kt` / Database:** Add `hasSeenBossLairPopup: Boolean` and `highestBossLairFloor: Int`.
2.  **`GameRepository.kt`:** Add methods to update the new state flags.
3.  **`TownScreen.kt`:** Implement the background cave sprite and the discovery popup logic.
4.  **`BossLairPrepScreen.kt`:** Create the new composable for pathfinding and hero assignment.
5.  **`FFBattleEngine.kt`:** Wrap spawn logic, stat scaling, and loot math in `if (mode == BOSS_LAIR)` checks.