package com.example.vikvakt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageHandler {

    /**
     * Ladda ner bilden från URL och returnera som en Bitmap.
     */
    fun downloadImage(imageUrl: String): Bitmap? {
        var bitmap: Bitmap? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(imageUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            val inputStream: InputStream = connection.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("ImageHandler", "Fel vid nedladdning: ${e.message}")
        } finally {
            connection?.disconnect()
        }
        return bitmap
    }

    fun extractPixels(bitmap: Bitmap): IntArray {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return pixels
    }
}
