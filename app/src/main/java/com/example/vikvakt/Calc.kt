package com.example.vikvakt
import kotlin.math.*
class Calc {




    companion object {
        fun wake(initialWind: Double, distance: Double, height: Double): Double {
            val drag = 1.2
            return initialWind - (1 - drag / (1 + distance / height))
        }

        fun twoDimensionsToOne(row: Int, width: Int, column: Int): Int {
            return row * width + column
        }
        fun oneDimensionToTwo(index: Int, width: Int): Pair<Int, Int> {
            val row = index / width
            val column = index % width
            return Pair(row, column)
        }

        fun getLinePositions(
            startX: Int,
            startY: Int,
            angle: Double,
            rows: Int,
            cols: Int
        ): MutableList<Int> {
            val positions = mutableListOf<Pair<Int, Int>>()
            var oneDimensionalList = mutableListOf<Int>()

            val theta = Math.toRadians(angle)

            // Steg för att röra sig i matrisen (x- och y-riktning)
            val dx = cos(theta)
            val dy = sin(theta)

            // Startkoordinater
            var x = startX.toDouble()
            var y = startY.toDouble()

            // Lägg till startpunkten
            positions.add(Pair(startX, startY))
            oneDimensionalList.add(twoDimensionsToOne(startX, 256, startY))
            // Iterera tills vi lämnar matrisens gränser
            while (x >= 0 && x < cols && y >= 0 && y < rows)
            {
                // Flytta ett litet steg framåt
                x += dx
                y += dy

                val gridX = x.toInt()
                val gridY = y.toInt()


                if (gridX in 0 until cols && gridY in 0 until rows) {
                    val newPosition = Pair(gridX, gridY)
                    if (!positions.contains(newPosition)) {
                        oneDimensionalList.add(twoDimensionsToOne(newPosition.first, 256, newPosition.second))
                        positions.add(newPosition)
                    }
                }
            }

            return oneDimensionalList
        }
    }
}