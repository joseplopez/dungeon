# Future Improvements: Final Dungeon

This document outlines potential future features, completed UI overhauls, and upcoming section redesigns for Final Dungeon, aimed at increasing player engagement, monetization, and visual polish.

---

## ✨ Visual & UI Overhauls

### 🏰 1. Procedural Dungeon Background System [COMPLETED]
*   **[x] 23 Unique Biome Backgrounds**: Procedural Canvas background graphics for every FF Dimension Biome:
    *   *Cornelia Castle, Chaos Shrine, Gurgu Volcano, Sea Shrine, Earth Cave, Crystal Tower, Mysidian Tower, Pandaemonium, Mount Ordeals, Baron Castle, Ancient Castle, Narshe Mines, Magitek Factory, Kefka Tower, Floating Continent, Midgar Sewers, Shinra Building, Northern Crater, Golden Saucer, Bevelle Temple, Omega Ruins, Sin Interior, Generic Dungeon*.
*   **[x] Animated Atmospheric FX**: Torch flicker, magma river sine waves, heat pulses, underwater light shafts, rising bubbles, falling embers, rotating Yevon symbols, cyber scanner sweeps, and perspective tiled floors.

### 🏙️ 2. Enhanced Town Visuals & NPC Life [COMPLETED]
*   **[x] High-Detail Building Sprites (`PixelTownBuildings.kt`)**:
    *   *Alchemy / Crystal Shop*: Purple gabled roof with chimney smoke, mounted glowing green Potion Flask emblem, shop display window with potions, wooden barrel.
    *   *Training Hall / Barracks*: Medieval Tudor style cream plaster with dark timber cross-beams, pitched tiled roof, candle-lit windows.
    *   *Adventurer's Guild / Relics*: Two-story stone manor with golden shield roof emblem, red crest banner, and interactive **BULLETIN** board with quest notices.
    *   *The Inn*: Cozy 2-story tavern with stone base, dark timber upper floor, swinging signboard, and warm lattice windows.
*   **[x] Animated Walking NPCs (`TownScreen.kt`)**:
    *   Guard 💂, Scholar 🧙, Hero 🗡️, and Merchant 🪙 strolling back and forth along the cobblestone street with walking bounce and direction flipping.
    *   Interactive speech bubbles displaying localized gameplay tips and lore when tapped or automatically during ambient walking.
*   **[x] Town Bulletin Board**: Interactive popup displaying highest floor record and active town perks summary.
*   **[x] Single Scroll Container Fix**: Seamless horizontal touch scrolling across all buildings, street ground, and NPCs.

### 💎 3. Legendary Relics Screen Dashboard [COMPLETED]
*   **[x] Procedural Relic Canvas Graphics (`RelicCanvasSprites.kt`)**:
    *   High-detail procedural pixel art for 12 Legendary Relics (*Runic Amulet, Chrono Pocket Watch, Orb of Power, Vitality Seed, Runic Aegis, Berserker Ring, Avarice Coin, Astral Prism, Magnetic Compass, Dimensional Pouch, Double Chest, Soul Athanor*).
*   **[x] 3-Panel Legendary Inspector Dashboard (`RelicsScreen.kt`)**:
    *   *Left Panel*: `RELICS` / `AVAILABLE` tabs with a 3-column slot grid and active double gold border highlight.
    *   *Center Panel*: Large 130dp animated canvas preview box of selected relic, title, rarity badge, Magicite cost badge (`💎`), `PERK UPGRADES` dashed divider line, and stat boost cards displaying `Current Bonus -> Next Level Bonus` with matching stat icons (❤️, 🛡️, 🪙, etc.) and large golden `UPGRADE` button.
    *   *Right Panel*: Active Perks Summary list showing all unlocked relics, icons, and total stat bonus percentage.

---

## 🚀 Upcoming Section Redesigns (Following the 3-Panel Inspector Pattern)

### 🎒 4. Equipment & Inventory Screen Overhaul [NEXT]
*   **3-Panel Equipment Inspector**:
    *   *Left Panel*: Hero Party Selector & Equipment Slots (*Weapon, Armor, Accessory, Helmet*).
    *   *Center Panel*: Large animated Hero Sprite Canvas Preview with glowing rarity aura, equipped gear badges, and `STAT OVERVIEW` cards (*ATK, DEF, MAG, HP, CRIT %, CRIT DMG*).
    *   *Right Panel*: Inventory Item Grid sorted by Rarity (*Common, Rare, Epic, Legendary, Mythic*) with detailed item comparison popup and one-tap `EQUIP BEST` / `UNEQUIP ALL`.

### 📈 5. Job Masteries & Training Grounds Overhaul
*   **3-Panel Training Dashboard**:
    *   *Left Panel*: Job Class Selector Grid (*Warrior, White Mage, Black Mage, Thief, Knight, Ninja, Monk, Dragoon, Summoner, Red Mage, Time Mage, Bard*).
    *   *Center Panel*: Large animated Job Sprite Preview, Job Level, EXP progress bar, and Mastered Stat Perk Cards.
    *   *Right Panel*: Pets & Companions List with level-up progress and passive support buff badges.

### 🏆 6. Hall of Fame & Leaderboards Overhaul
*   **3-Panel Trophy & Ranking Dashboard**:
    *   *Left Panel*: Ranking Category Tabs (*Global, Dimension, Friends, Fastest Clear*).
    *   *Center Panel*: Top Player Ghost Run Inspector — Team Composition, Job Classes, Relics equipped, and Max Floor Reached.
    *   *Right Panel*: Personal Rank Badge, Best Floor Record, and Dimension Progress Trophies.

### 💥 7. Combat Polish & Particle FX
*   **Spell Visual Effects**: Pixel particle bursts for Fire, Ice, Lightning, Cure, Holy, and Meteor spells.
*   **Screen Shake & Damage Numbers**: Subtle screen shake feedback for heavy boss attacks and golden glowing floating critical damage numbers.

---

## 🏆 Social & Competition
*   **[x] Player Rankings (Leaderboards)**:
    *   **[x] Global/Friends Leaderboards**: Ranked by "Max Floor Reached", "Total Magicite Earned", and "Fastest Dimension Clear".
    *   **[x] Dimension-Specific Rankings**: Hall of Fame integration to show how players compare within each Dimension.
    *   **[x] Ghost Runs**: A feature to view the team composition and gear of top-ranking players.

## 💰 Monetization & Economy
*   **[x] Interstitial Ads**:
    *   **[x] Gold/Magicite Boost**: Option to watch a short ad for a temporary 2x Gil or Magicite multiplier during a run.
    *   **[x] Revive Team**: Once per run, watch an ad to revive a fallen team at the start of the current floor.

## 🎮 Gameplay & Progression
*   **[x] Support & Feedback System**:
    *   **[x] Email Support**: Direct button to email the developer (`joseplcatz@gmail.com`) for bugs and suggestions.
    *   **[x] Play Store Rating**: Direct link to rate the app on Google Play.
*   **[x] Advanced Job Masteries**:
    *   **[x] Job Levels**: Earning EXP for a specific Job to unlock permanent minor stat boosts for all heroes of that class.
    *   **[x] Hidden Jobs**: Ultra-rare jobs (e.g., Onion Knight, Mime) unlocked through secret achievements or specific Relic combinations.
*   **[x] Pet System**:
    *   **[x] Support Familiars**: Non-combat pets that provide passive buffs (e.g., +5% Item Find) or occasionally cast minor support spells.
    *   **[x] Support Familiars Level Up**: Pets level up and increase passive stat boosts.
*   **Daily Challenges**:
    *   **Cursed Dungeons**: Daily runs with specific negative modifiers (e.g., "Magic Costs 2x") but significantly higher rewards.
*   **Dimension Rewards & Lore**:
    *   Advancing between dimensions grants main protagonist jobs (e.g., Dimension 1 unlocks Warrior of Light).
    *   Adding dimension-specific stories and boss difficulty tied to Final Fantasy lore.

## 🛠️ Technical Debt & Tools
*   **Modding Support**: [ON HOLD - RETHINK] External JSON/XML definitions for Jobs and Items.
*   **[x] Enhanced Analytics**: Firebase Analytics integration tracking player progression, town upgrades, and dungeon runs.
