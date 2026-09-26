# AI Agent Operating Protocol

To prevent critical errors such as data loss or localization regressions, all AI agents operating on this codebase MUST follow these mandatory rules:

## 🔍 Contextual Awareness
Before making ANY changes to the project, the agent MUST read the following "Skill" files to understand the specific protocols of this repository:
1.  **`SKILL_DATABASE_MANAGEMENT.md`**: Mandatory steps for Room database changes (Version increments, explicit migrations, and `ensureColumn` safety).
2.  **`SKILL_LOCALIZATION_PROTOCOL.md`**: Rules for string management, extraction, and XLIFF placeholder usage.
3.  **`SKILL_TESTING_PROTOCOL.md`**: Mandatory requirements for UI and database migration testing.
4.  **`SKILL_BIOME_BACKGROUNDS.md`**: Mandatory 7-layer architecture and quality standards for UI biome backgrounds in `PixelBackground.kt`.

## 🛡️ Database Safety (Zero-Data-Loss Policy)
*   **NEVER** change an `@Entity` without incrementing the version in `GameDatabase.kt`.
*   **NEVER** rely on `fallbackToDestructiveMigration` for production-like changes.
*   **ALWAYS** write an explicit migration using the `try-catch` and `ensureColumn` pattern defined in `SKILL_DATABASE_MANAGEMENT.md`.

## 🌍 Localization Safety
*   **NEVER** use hardcoded string literals in Compose.
*   **ALWAYS** extract strings to `strings.xml` and use `<xliff:g>` for variables.

## 🧪 Testing Safety
*   **ALWAYS** add UI tests for new features.
*   **ALWAYS** verify database migrations with automated tests.
*   **NEVER** consider a task "done" until `./gradlew connectedDebugAndroidTest` passes on the relevant modules.

## 📝 Modification Log
Agents should note in their response which skills they consulted before applying changes.
