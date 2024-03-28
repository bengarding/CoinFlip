package com.helsinkiwizard.cointoss.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

@AndroidEntryPoint
class ImageCropActivity : ComponentActivity() {

    private fun storeBitmap(imageBitmap: ImageBitmap): Uri? {
        val bitmap = imageBitmap.asAndroidBitmap()
        val compressFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.JPEG
        }
        val fileType = if (compressFormat == Bitmap.CompressFormat.JPEG) "jpg" else "webp"
        val imageName = Random.nextInt()
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$imageName.$fileType")

        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(compressFormat, 100, out)
            }
            FileProvider.getUriForFile(this, "$packageName.file-provider", file)
        } catch (e: IOException) {
            null
        }
    }
}
