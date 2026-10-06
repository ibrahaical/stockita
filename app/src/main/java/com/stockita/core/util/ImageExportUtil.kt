package com.stockita.core.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ImageExportUtil {

    /**
     * Saves a Bitmap to the app's cache directory and returns its content Uri.
     */
    fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "struk_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Log.e("ImageExportUtil", "Error saving bitmap", e)
            null
        }
    }

    /**
     * Shares an image Uri to WhatsApp specifically, or falls back to system share if not installed.
     */
    fun shareImageToWhatsApp(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Terima kasih telah berbelanja!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Try specifically for WhatsApp first
        intent.setPackage("com.whatsapp")
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // WhatsApp is not installed, fallback to chooser
            val chooserIntent = Intent.createChooser(intent.apply { setPackage(null) }, "Bagikan Struk via")
            context.startActivity(chooserIntent)
        }
    }
}
