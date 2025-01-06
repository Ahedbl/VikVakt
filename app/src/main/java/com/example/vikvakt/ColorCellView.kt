package com.example.vikvakt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class ColorCellView(context: Context, private val value: Double) : View(context) {

    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)

        // Mappa värdet till en färg
        var color = getColorForValue(value)

        // Rita cellen
        val paint = Paint().apply {
            color = color
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }


    // Mappa matrisvärdet till en färg (t.ex., blått till rött)
    private fun getColorForValue(value: Double): Int {
        // Mappa värde mellan 0 och 50 till en färgskala
        val scaledValue = value.coerceIn(0.0, 50.0)  // Säkerställ att värdet är mellan 0 och 50

        // Här gör vi en linjär övergång från blått (0) till rött (50)
        val red = (scaledValue / 50 * 255).toInt()  // Värdet 50 ger röd färg
        val green = 0  // Håller grönt på 0 för enkelhetens skull
        val blue = (255 - (scaledValue / 50 * 255)).toInt()  // Värdet 0 ger blå färg

        return Color.rgb(red, green, blue)
    }
}
