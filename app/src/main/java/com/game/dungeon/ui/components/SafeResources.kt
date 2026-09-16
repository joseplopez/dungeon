package com.game.dungeon.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.annotation.StringRes
import com.game.dungeon.ui.viewmodels.DungeonViewModel

@Composable
fun safeStringResource(@StringRes id: Int, vararg args: Any): String {
    val context = LocalContext.current
    return remember(id, *args) {
        val rawString = try {
            context.getString(id)
        } catch (e: Exception) {
            ""
        }
        
        try {
            if (args.isEmpty()) rawString else String.format(rawString, *args)
        } catch (e: Exception) {
            formatFallback(rawString, args.toList())
        }
    }
}

fun formatSafeLogEntry(context: android.content.Context, entry: DungeonViewModel.FFLogEntry): String {
    val rawString = try {
        context.getString(entry.messageRes)
    } catch (e: Exception) {
        "Log Entry"
    }

    return try {
        if (entry.args.isEmpty()) rawString else String.format(rawString, *entry.args.toTypedArray())
    } catch (e: Exception) {
        formatFallback(rawString, entry.args)
    }
}

private fun formatFallback(rawString: String, args: List<Any>): String {
    return try {
        val sanitized = rawString.replace(Regex("%([0-9]+\\$)?[^a-zA-Z]*[a-zA-Z]")) { matchResult ->
            val index = matchResult.groups[1]?.value ?: ""
            "%${index}s"
        }
        
        // Count specifiers to ensure we don't pass too few/many
        // This is a simple heuristic: count % that aren't followed by another %
        var specifierCount = 0
        val matcher = java.util.regex.Pattern.compile("%([0-9]+\\$)?[^a-zA-Z]*[a-zA-Z]").matcher(sanitized)
        while (matcher.find()) specifierCount++

        val finalArgs = if (args.size >= specifierCount) {
            args.take(specifierCount).toTypedArray()
        } else {
            (args + List(specifierCount - args.size) { "?" }).toTypedArray()
        }
        
        String.format(sanitized, *finalArgs)
    } catch (e: Exception) {
        "$rawString $args"
    }
}
