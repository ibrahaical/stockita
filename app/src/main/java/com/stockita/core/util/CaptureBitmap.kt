package com.stockita.core.util

import android.graphics.Bitmap
import android.graphics.Picture
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

/**
 * Creates a modifier that records the drawing into a Picture, 
 * which can later be converted into a Bitmap.
 */
fun Modifier.captureToPicture(picture: Picture): Modifier = this.drawWithCache {
    val width = this.size.width.toInt()
    val height = this.size.height.toInt()
    
    onDrawWithContent {
        val pictureCanvas = Canvas(picture.beginRecording(width, height))
        
        draw(this, this.layoutDirection, pictureCanvas, this.size) {
            this@onDrawWithContent.drawContent()
        }
        picture.endRecording()
        
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawPicture(picture)
        }
    }
}

/**
 * Converts a Picture to a Bitmap.
 */
fun Picture.createBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        this.width.takeIf { it > 0 } ?: 1,
        this.height.takeIf { it > 0 } ?: 1,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    canvas.drawPicture(this)
    return bitmap
}
