package com.helsinkiwizard.core.utils

import java.util.Locale

/**
 * Converts a string into sentence case (first word capitalized).
 */
fun String.sentenceCase(): String {
    return this.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

/**
 * Returns this string if it's not null or empty or the result of calling defaultValue function if the string
 * is null or empty.
 */
inline fun String?.ifNullOrEmpty(defaultValue: () -> String): String {
    return if (this.isNullOrEmpty()) defaultValue() else this
}
