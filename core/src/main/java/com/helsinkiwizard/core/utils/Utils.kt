package com.helsinkiwizard.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

fun getEmailIntent(email: String): Intent {
    return Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    }
}

fun storeBitmap(context: Context,
    bitmap: Bitmap?,
    name: Int? = null
): Uri? {
    val compressFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        Bitmap.CompressFormat.JPEG
    }
    val fileType = if (compressFormat == Bitmap.CompressFormat.JPEG) "jpg" else "webp"
    val imageName = name ?: Random.nextInt()
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$imageName.$fileType")

    return try {
        FileOutputStream(file).use { out ->
            bitmap?.compress(compressFormat, 100, out)
        }
        FileProvider.getUriForFile(context, "${context.packageName}.file-provider", file)
    } catch (e: IOException) {
        Log.e("Utils", "storeBitmap", e)
        null
    }
}
