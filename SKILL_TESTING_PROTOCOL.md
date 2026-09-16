# Skill: Testing & Quality Assurance Protocol

This document defines the mandatory testing strategy for all new features and modifications to this project. 

## 🧪 Testing Protocol

1.  **Mandatory UI Tests**: Every new screen or major UI feature MUST have corresponding instrumented UI tests in `app/src/androidTest/java/com/game/dungeon/ui/`.
    *   Tests should verify initial state, navigation, and core interactions.
    *   Use `composeTestRule` to interact with UI elements.
    *   Prefer `onNodeWithText` or `onNodeWithTag` (if tags are implemented).
    *   Handle horizontal scrolling explicitly if the feature is in a scrollable container.

2.  **Mandatory Migration Tests**: Every database schema change MUST be verified with a migration test in `app/src/androidTest/java/com/game/dungeon/data/db/MigrationTest.kt`.
    *   Ensure data integrity is preserved during migration.
    *   Verify the latest schema can be initialized correctly.
    *   If specific data migrations are complex, add a dedicated test case using `MigrationTestHelper`.

3.  **Continuous Validation**: Before finalizing any task, the agent MUST run the existing test suite to ensure no regressions were introduced.
    *   Command: `./gradlew connectedDebugAndroidTest`
    *   Iterate and fix until all tests are GREEN.

## 🛠️ Testing Tools & Patterns

### UI Test Pattern
```kotlin
@HiltAndroidTest
class NewFeatureUITest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testFeatureInteraction() {
        composeTestRule.waitForIdle()
        // Interaction logic
        composeTestRule.onNodeWithText("FEATURE_BUTTON").performClick()
        composeTestRule.onNodeWithText("EXPECTED_RESULT").assertIsDisplayed()
    }
}
```

### Migration Test Pattern
```kotlin
@Test
fun testMigrationXtoY() {
    helper.createDatabase(TEST_DB, X).apply {
        // Insert test data
        close()
    }
    // Run migration and validate
    helper.runMigrationsAndValidate(TEST_DB, Y, true, GameDatabase.MIGRATION_X_Y)
}
```

## 📝 Feature Completion Checklist

- [ ] New UI features have instrumented tests covering main flows.
- [ ] Database changes have corresponding migration tests.
- [ ] `./gradlew connectedDebugAndroidTest` executed and all tests passed.
- [ ] Regressions checked for existing screens (Inn, Town, Dungeon, etc.).

*Always read this file before implementing new features or modifying existing logic.*
