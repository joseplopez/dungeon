# AI Agent Architecture Map & Operating Protocol

## Core Invariants (CRITICAL - DO NOT BREAK)
1. **Item Recovery Invariant**: Hero removal, dismissal, or party death MUST return all equipped gear/items to inventory via `GameRepository`. Items must never be lost.
2. **Stat Mutation Invariant**: Combat stats in `FFBattleEngine` MUST be calculated strictly as: Base Stats (`Hero.kt`) x Relic Multipliers (`RelicType.kt`) + Gear Stats (`Item.kt`).
3. **Room Migration Rule**: All database updates require strict schema preservation with explicit migration scripts in `GameDatabase.kt` (v12+). `fallbackToDestructiveMigration` is strictly prohibited.
4. **Boss Loot Rule**: Defeating dungeon bosses MUST always drop RARE or higher quality items, influenced by Magicite Magnet relics.

## 🔍 Contextual Awareness
Before making ANY changes to the project, the agent MUST read the following "Skill" files to understand the specific protocols of this repository:
1.  **`SKILL_DATABASE_MANAGEMENT.md`**: Mandatory steps for Room database changes (Version increments, explicit migrations, and `ensureColumn` safety).
2.  **`SKILL_LOCALIZATION_PROTOCOL.md`**: Rules for string management, extraction, and XLIFF placeholder usage.
3.  **`SKILL_TESTING_PROTOCOL.md`**: Mandatory requirements for UI and database migration testing.
4.  **`SKILL_BIOME_BACKGROUNDS.md`**: Mandatory 7-layer architecture and quality standards for UI biome backgrounds in `PixelBackground.kt`.
5.  **`SKILL_HERO_SPRITES.md`**: Mandatory pixel-by-pixel JRPG architecture, uniform matrix grid mapping, palettes, and quality standards for Hero and Monster sprites in `PixelCharacters.kt`.
6.  **`SKILL_SPRITE_CONVERSION.md`**: Protocol, architecture, color palette mapping, and workflow for image-to-matrix conversion via `convert.py`.

## Core Package Structure (`com.game.dungeon`)
- **`data/models`**: Domain entities (`Hero`, `Item`, `GameState`, `Enemy`, `RelicType`).
- **`data/db`**: Room setup (`GameDatabase` v12+), DAOs (`GameStateDao`, `HeroDao`, `ItemDao`).
- **`data/repository`**: Single-source-of-truth orchestration (`GameRepository`).
- **`engine/`**: ATB combat loop, stat computations, action resolution (`FFBattleEngine`).
- **`ui/screens` & `ui/viewmodels`**: UI screens and ViewModels (`Dungeon`, `Equipment`, `Inn`, `Town`, `Relics`).

## Safety Protocols
- **Database**: Never modify an `@Entity` without incrementing database version and writing explicit migrations.
- **Localization**: Never hardcode string literals in Compose; use `strings.xml`.
- **Testing**: Verify database migrations and Unit test flow changes before finishing tasks.