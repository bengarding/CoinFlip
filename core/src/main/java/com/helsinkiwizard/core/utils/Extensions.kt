package com.helsinkiwizard.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.IOException
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

fun Uri.toBitmap(context: Context): Bitmap? {
    return try {
        context.contentResolver.openInputStream(this)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: IOException) {
        Logger.e("Extensions", "toBitmap failed", e)
        null
    }
}
