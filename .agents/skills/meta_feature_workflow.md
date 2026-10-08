# Skill: Token-Efficient Feature Workflow

## Purpose
Enforces absolute token conservation when implementing new features, refactoring, or modifying game mechanics.

---

## 1. Execution Constraints (Strict Token Saver)
- **Zero Discovery Scans**: Do NOT run global workspace scans, directory tree listings, or file searches (`find`/`grep`).
- **File Scope Isolation**: Access ONLY the `@file` references explicitly passed in the user request and the matching `.agents/skills/feature_*.md` contract files.
- **Single-Pass Output**: Generate complete Kotlin/Compose/Room edits in a single turn. Do not ask for confirmation before writing code unless a non-negotiable invariant is broken.
- **Ultra-Dense Summaries**: End responses with maximum 3 lines summarizing created/modified files and state changes.

---

## 2. Skill-Routing Protocol
Before inspecting code, map the request to its corresponding contract file:
- Combat / Damage / Turn loop -> `.agents/skills/feature_combat.md`
- Items / Inventory / Quick Equip -> `.agents/skills/feature_equipment.md`
- Prestige / Inn / Hiring / Dimensions -> `.agents/skills/feature_inn_prestige.md`
- Relics / Town upgrades / Magicite -> `.agents/skills/feature_relics.md`
- Job EXP / Abilities / Passives -> `.agents/skills/feature_mastery.md`
- Biomes / Graphics / Boss Spawns -> `.agents/skills/feature_biomes.md`
- Leaderboards / Scores -> `.agents/skills/feature_leaderboard.md`
- Purchases / Ads / Currency -> `.agents/skills/feature_monetization.md`
- Pets / Companions -> `.agents/skills/feature_pets.md`
- Cloud Sync / Auth -> `.agents/skills/feature_auth_sync.md`
- Music / SFX -> `.agents/skills/feature_audio.md`

---

## 3. Implementation Workflow
1. **Invariant Check**: Read the mapped `.agents/skills/feature_*.md` for invariants (e.g., Hero death gear recovery, Room v12 migration, Relic cost formulas).
2. **Target File Edits**: Apply code changes strictly to target files.
3. **Skill Synchronization**:
   - If new cross-system contracts or data flows are introduced, update the corresponding `.agents/skills/feature_*.md` file in the same response.
   - If an entirely new domain is created, output a new concise `.agents/skills/feature_<new_domain>.md` file (max 150 words).

---

## 4. Response Template
- **Changes Applied**: `[File1.kt]`, `[File2.kt]`
- **Invariants Checked**: `[Verified rule]`
- **Skill Updated**: `[Yes/No - Name of updated skill]`