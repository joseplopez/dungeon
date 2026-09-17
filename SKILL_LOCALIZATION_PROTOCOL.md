# Skill: Localization & String Management Protocol

This document defines the mandatory strategy for handling user-facing strings in this project to support localization across 17 languages.

## 🌍 Localization Protocol

1.  **Zero Hardcoded Strings**: No user-facing string should ever be hardcoded in Kotlin or XML files.
2.  **Explicit Resource Extraction**: Every new string must be added to `app/src/main/res/values/strings.xml`.
3.  **Mandatory Global Translation**: Every time a new string is created, it **MUST** be translated to all 16 additional languages we support:
    - Spanish (es), Portuguese (pt), Swahili (sw), Bengali (bn), Turkish (tr), Arabic (ar), Russian (ru), Ukrainian (uk), Urdu (ur), Catalan (ca), Japanese (ja), French (fr), German (de), Italian (it), Korean (ko), and Thai (th).
4.  **No XLIFF Tags**: Do not use `<xliff:g>` tags in the English `strings.xml`. Keep translations clean using standard placeholders (e.g., `%1$s`, `%1$d`).
5.  **Non-Translatable Strings**: Mark technical strings or brand names with `translatable="false"` if necessary.

## 🛠️ Placeholder Standards

Always use the following format for strings with variables:

```xml
<string name="example_format">Value is %1$d</string>
```

In Compose:
```kotlin
stringResource(R.string.example_format, dynamicValue)
```

## 📝 Feature Completion Checklist

Before finalizing any feature, perform a "String Audit":
- [ ] Scan all new `@Composable` functions for `""` literals.
- [ ] Verify all strings are extracted to `strings.xml`.
- [ ] **Mandatory**: Ensure the new strings are translated in ALL 17 `strings.xml` files.
- [ ] Ensure `stringResource()` is used for all UI text.

*Always read this file before creating new UI components or screens.*
