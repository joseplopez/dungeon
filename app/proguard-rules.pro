# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Gson models
-keep class com.game.dungeon.data.models.** { *; }

# Keep Room entities (usually handled by Room but safe to keep)
-keep class com.game.dungeon.data.db.** { *; }

# Keep Hilt generated classes (usually handled by Hilt)
-keep class **_HiltModules* { *; }

# Preserve line numbers for better crash reports in Firebase Crashlytics
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Dungeon battle log entries and their data to prevent IllegalFormatConversionException
-keep class com.game.dungeon.ui.viewmodels.DungeonViewModel$FFLogEntry { *; }
-keep class com.game.dungeon.ui.viewmodels.DungeonViewModel$LogType { *; }
