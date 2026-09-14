# Skill: Localization & String Management Protocol

This document defines the mandatory strategy for handling user-facing strings in this project to support localization via Google Play Translation.

## 🌍 Localization Protocol

1.  **Zero Hardcoded Strings**: No user-facing string should ever be hardcoded in Kotlin or XML files.
2.  **Explicit Resource Extraction**: Every new string must be added to `app/src/main/res/values/strings.xml`.
3.  **Placeholder Management**: Use `<xliff:g>` tags for any dynamic content (numbers, names, stats) to prevent translators from modifying placeholders.
4.  **Non-Translatable Strings**: Mark technical strings or brand names with `translatable="false"` if necessary.

## 🛠️ Placeholder Standards

Always use the following format for strings with variables:

```xml
<string name="example_format">Value is <xliff:g id="val_name">%1$d</xliff:g></string>
```

In Compose:
```kotlin
stringResource(R.string.example_format, dynamicValue)
```

## 📝 Feature Completion Checklist

Before finalizing any feature, perform a "String Audit":
- [ ] Scan all new `@Composable` functions for `""` literals.
- [ ] Verify all numbers or names in strings are wrapped in `<xliff:g>`.
- [ ] Ensure `stringResource()` is used for all UI text.

*Always read this file before creating new UI components or screens.*
