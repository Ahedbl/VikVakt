package com.example.vikvakt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WindOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 5f
    }

    private var windSpeed: Float = 0f
    private var windDirection: Float = 0f
    private var elevationArray: IntArray? = null
    private var bitmapWidth = 0
    private var bitmapHeight = 0

    fun updateWindData(speed: Float, direction: Float, elevationData: IntArray, width: Int, height: Int) {
        windSpeed = speed
        windDirection = direction
        elevationArray = elevationData
        bitmapWidth = width
        bitmapHeight = height
        invalidate() // Tvinga omritning
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Rita overlay baserat på vind och höjddata
        elevationArray?.let { elevationData ->
            for (y in 0 until bitmapHeight) {
                for (x in 0 until bitmapWidth) {
                    val index = y * bitmapWidth + x
                    val elevation = elevationData[index]

                    // Exempel: Skapa färg baserat på elevation
                    val color = Color.argb(128, (elevation / 100).coerceAtMost(255), 0, 255 - (elevation / 100).coerceAtMost(255))
                    paint.color = color

                    // Rita en liten fyrkant som representerar pixeln
                    canvas.drawRect(x * 10f, y * 10f, (x + 1) * 10f, (y + 1) * 10f, paint)
                }
            }
        }

        // Rita vindriktning som en pil
        paint.color = Color.BLACK
        paint.strokeWidth = 10f
        val centerX = width / 2f
        val centerY = height / 2f
        val arrowLength = windSpeed * 10 // Skala hastigheten
        val endX = centerX + arrowLength * Math.cos(Math.toRadians(windDirection.toDouble())).toFloat()
        val endY = centerY - arrowLength * Math.sin(Math.toRadians(windDirection.toDouble())).toFloat()

        canvas.drawLine(centerX, centerY, endX, endY, paint)
    }
}
