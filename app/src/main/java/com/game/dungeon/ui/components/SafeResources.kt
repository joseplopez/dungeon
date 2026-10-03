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
            if (args.isEmpty()) rawString else String.format(sanitizeFormatString(rawString), *args)
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
        if (entry.args.isEmpty()) rawString else String.format(sanitizeFormatString(rawString), *entry.args.toTypedArray())
    } catch (e: Exception) {
        formatFallback(rawString, entry.args)
    }
}

private fun sanitizeFormatString(rawString: String): String {
    if (!rawString.contains("%")) return rawString

    val specifierRegex = Regex("%(?:[0-9]+\\$)?[-+0, '(#_]*[0-9]*(?:\\.[0-9]+)?[a-zA-Z%n]")
    val sb = StringBuilder()
    var i = 0
    while (i < rawString.length) {
        if (rawString[i] == '%') {
            val match = specifierRegex.matchAt(rawString, i)
            if (match != null) {
                sb.append(match.value)
                i += match.value.length
            } else {
                sb.append("%%")
                i++
            }
        } else {
            sb.append(rawString[i])
            i++
        }
    }
    return sb.toString()
}

private fun formatFallback(rawString: String, args: List<Any>): String {
    return try {
        val sanitized = sanitizeFormatString(rawString)
        var specifierCount = 0
        val matcher = java.util.regex.Pattern.compile("%(?:[0-9]+\\$)?[-+0, '(#_]*[0-9]*(?:\\.[0-9]+)?[a-zA-Z]").matcher(sanitized)
        while (matcher.find()) specifierCount++

        val finalArgs = if (args.size >= specifierCount) {
            args.take(specifierCount).toTypedArray()
        } else {
            (args + List(specifierCount - args.size) { "?" }).toTypedArray()
        }
        
        String.format(sanitized, *finalArgs)
    } catch (e: Exception) {
        var result = rawString
        args.forEach { arg ->
            result = result.replaceFirst(Regex("%([0-9]+\\$)?[^a-zA-Z]*[a-zA-Z]"), arg.toString())
        }
        result.replace("%%", "%")
    }
}
